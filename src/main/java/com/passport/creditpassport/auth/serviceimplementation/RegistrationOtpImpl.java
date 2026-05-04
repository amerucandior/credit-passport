package com.passport.creditpassport.auth.serviceimplementation;

import com.passport.creditpassport.auth.models.User;
import com.passport.creditpassport.auth.service.EmailService;
import com.passport.creditpassport.auth.service.OtpService;
import com.passport.creditpassport.auth.repository.UsersRepository;
import com.passport.creditpassport.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationOtpImpl {

    private static final Duration OTP_TTL = Duration.ofMinutes(10);

    private final EmailService emailService;
    private final UsersRepository usersRepository;

    // -----------------------------------------------------------------------
    // Send / Resend
    // -----------------------------------------------------------------------

    /**
     * Generates, hashes, and persists a registration OTP, then emails it.
     * Safe to call on both first send and resend.
     */
    @Transactional
    public void sendRegistrationOtp(String email) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String otpCode = OtpService.generateOtpCode();
        String otpHash = OtpService.hashOtp(otpCode);

        user.setRegistrationOtp(otpHash);
        user.setRegistrationOtpExpiresAt(OtpService.expiryInstant(OTP_TTL));
        usersRepository.save(user);

        emailService.sendOtpEmail(email, "Your registration code", otpCode);
        log.info("Registration OTP sent to {}", email);
//        emailService.sendSimpleEmail(email, "Your registration code", otpCode);
    }

    /**
     * Resends the OTP only when the account has not yet been verified.
     */
    @Transactional
    public void resendRegistrationOtp(String email) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new IllegalStateException("Account is already verified.");
        }

        sendRegistrationOtp(email);
    }

    // -----------------------------------------------------------------------
    // Verify
    // -----------------------------------------------------------------------

    /**
     * Verifies the OTP and enables the account on success.
     * Clears the OTP fields afterwards so they cannot be reused.
     *
     * @throws IllegalStateException    if the account is already verified
     * @throws IllegalArgumentException if the OTP is expired or incorrect
     */
    @Transactional
    public void verifyRegistrationOtp(String email, String rawOtp) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new IllegalStateException("Account is already verified.");
        }
        if (OtpService.isValid(user.getRegistrationOtpExpiresAt())) {
            throw new IllegalArgumentException("Registration OTP has expired.");
        }
        if (OtpService.verifyOtp(rawOtp, user.getRegistrationOtp())) {
            throw new IllegalArgumentException("Invalid registration OTP.");
        }

        user.setEnabled(true);
        user.setRegistrationOtp(null);
        user.setRegistrationOtpExpiresAt(null);
        usersRepository.save(user);

        log.info("Account verified for {}", email);
    }
}