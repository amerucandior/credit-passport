package com.passport.creditpassport.auth.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Stateless utility for OTP generation, hashing, and expiry.
 * No Spring beans — call these methods directly anywhere.
 */
public final class OtpUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();


    /** Returns a cryptographically random 6-digit code, zero-padded. */
    public static String generateOtpCode() {
        int code = SECURE_RANDOM.nextInt(1_000_000);
        return String.format("%06d", code);
    }

    /** BCrypt-hashes the raw OTP for safe storage. */
    public static String hashOtp(String rawOtp) {
        return ENCODER.encode(rawOtp);
    }

    /** Returns true when the raw OTP matches the stored BCrypt hash. */
    public static boolean verifyOtp(String rawOtp, String storedHash) {
        return ENCODER.matches(rawOtp, storedHash);
    }

    /** Returns the Instant at which an OTP generated now will expire. */
    public static Instant expiryInstant(Duration ttl) {
        return Instant.now().plus(ttl);
    }

    /** Returns true when the given expiry is still in the future. */
    public static boolean isValid(Instant expiresAt) {
        return expiresAt != null && Instant.now().isBefore(expiresAt);
    }
}