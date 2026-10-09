package com.passport.creditpassport.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Stateless utility for OTP generation, hashing, and expiry.
 * No Spring beans — call these methods directly anywhere.
 */
public final class OtpUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    public static final int MAX_ATTEMPTS = 10;

    /** Returns a cryptographically random 6-digit code, zero-padded. */
    public static String generateOtpCode() {
        int code = SECURE_RANDOM.nextInt(1_000_000);
        return String.format("%06d", code);
    }

    /** SHA256-hashes the raw OTP for safe storage. */
    public static String hashOtp(String rawOtp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawOtp.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash otp code", e);
        }
    }

    /** Returns true when the raw OTP matches the stored SHA256 hash, compared in constant time. */
    public static boolean verifyOtp(String rawOtp, String storedHash) {
        if (rawOtp == null || storedHash == null) {
            return false;
        }
        byte[] hash = hashOtp(rawOtp).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(hash, storedHash.getBytes(StandardCharsets.UTF_8));
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