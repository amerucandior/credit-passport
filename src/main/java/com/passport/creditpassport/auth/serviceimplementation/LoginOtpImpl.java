package com.passport.creditpassport.auth.serviceimplementation;

import com.passport.creditpassport.auth.Dto.AuthResponse;
import com.passport.creditpassport.auth.models.user;
import com.passport.creditpassport.auth.service.EmailService;
import com.passport.creditpassport.auth.service.OtpService;
import com.passport.creditpassport.auth.repository.UsersRepository;
import com.passport.creditpassport.config.JwtService;
import com.passport.creditpassport.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
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
    private final EmailService emailService;
    private final AuthServiceImpl authServiceImpl;


    /**
     * Authenticates email + password, rejects unverified accounts,
     * then generates and emails a login OTP. No JWT is issued here.
     */
    @Transactional
    public void initiateLogin(String identifier, String password) {
        Authentication authentication = authServiceImpl.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, password)
        );

        user user = (user) authentication.getPrincipal();

        assert user != null;
        if (!user.isEnabled()) {
            throw new IllegalStateException("Account not verified. Complete email verification first.");
        }

        String otpCode = OtpService.generateOtpCode();
        String otpHash = OtpService.hashOtp(otpCode);

        user.setLoginOtp(otpHash);
        user.setLoginOtpExpiresAt(OtpService.expiryInstant(OTP_TTL));
        usersRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), "Your login code", otpCode);
    }

    /**
     * Verifies the login OTP and issues a signed JWT on success.
     * This is the only place in the codebase that calls JwtService.generateToken().
     *
     * @return AuthResponse containing the JWT
     * @throws IllegalArgumentException if the OTP is expired or incorrect
     */
    @Transactional
    public AuthResponse verifyLoginOtp(String email, String rawOtp) {
        user user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!OtpService.isValid(user.getLoginOtpExpiresAt())) {
            throw new IllegalArgumentException("Login OTP has expired.");
        }
        if (!OtpService.verifyOtp(rawOtp, user.getLoginOtp())) {
            throw new IllegalArgumentException("Invalid login OTP.");
        }

        // Clear OTP fields — one-time use only
        user.setLoginOtp(null);
        user.setLoginOtpExpiresAt(null);
        usersRepository.save(user);

        String token = JwtService.generateToken(user.getNatId());
        log.info("Login successful for user {} email {}", user.getName(), user.getEmail());

        return AuthResponse.builder().token(token).build();
    }
}