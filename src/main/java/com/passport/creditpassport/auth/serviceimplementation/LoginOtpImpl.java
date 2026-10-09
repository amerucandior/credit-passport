package com.passport.creditpassport.auth.serviceimplementation;

import com.passport.creditpassport.auth.dto.AuthResponse;
import com.passport.creditpassport.auth.dto.OtpPayload;
import com.passport.creditpassport.auth.models.User;
import com.passport.creditpassport.auth.service.OtpUtils;
import com.passport.creditpassport.auth.UsersRepository;
import com.passport.creditpassport.auth.JwtService;
import com.passport.creditpassport.exception.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginOtpImpl {

    private static final Duration OTP_TTL = Duration.ofMinutes(10);

    private final UsersRepository usersRepository;
    private final AuthServiceImpl authServiceImpl;
    private final JwtService jwtService;


    /**
     * Authenticates email and password, rejects unverified accounts,
     * then generates and emails a login OTP. No JWT is issued here.
     */
    @Transactional
    public OtpPayload initiateLogin(String identifier, String password) {
        Authentication authentication = authServiceImpl.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, password)
        );

        User user = (User) authentication.getPrincipal();

        if (!user.isEnabled()) {
            throw new AccountNotVerifiedException("Account not verified. Complete email verification first.");
        }

        String otpCode = OtpUtils.generateOtpCode();
        String otpHash = OtpUtils.hashOtp(otpCode);

        user.setLoginOtp(otpHash);
        user.setLoginOtpExpiresAt(OtpUtils.expiryInstant(OTP_TTL));
        user.setLoginOtpAttempts(0);
        usersRepository.save(user);

        return new OtpPayload(user.getEmail(), otpCode);
    }

    /**
     * Verifies the login OTP and issues a signed JWT on success.
     * This is the only place in the codebase that calls JwtService.generateToken().
     *
     * @return AuthResponse containing the JWT
     * @throws InvalidOtpException if the OTP is expired or incorrect
     */
    @Transactional
    public AuthResponse verifyLoginOtp(String email, String otpCode) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!OtpUtils.isValid(user.getLoginOtpExpiresAt())) {
            throw new OtpExpiredException("Login OTP has expired.");
        }
        if (!OtpUtils.verifyOtp(otpCode, user.getLoginOtp())) {
            int attempts = user.getLoginOtpAttempts() + 1;
            user.setLoginOtpAttempts(attempts);

            if (attempts >= OtpUtils.MAX_ATTEMPTS) {
                user.setLoginOtp(null);
                user.setLoginOtpExpiresAt(null);
                user.setLoginOtpAttempts(0);
                usersRepository.save(user);
                throw new OtpMaxAttemptsExceededException(
                        "Too many incorrect attempts. Request a new login OTP.");
            }

            usersRepository.save(user);
            throw new InvalidOtpException("Invalid login OTP.");
        }

        // Clear OTP fields — one-time use only
        user.setLoginOtp(null);
        user.setLoginOtpExpiresAt(null);
        usersRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getNatId(), user.getName(), user.getNumber(), user.getEmail(), user.getUserType().name());
        log.info("Login successful for user {} email {}", user.getName(), user.getEmail());

        return AuthResponse.builder().token(token).build();
    }
}