package com.passport.creditpassport.lender.util;

import java.security.MessageDigest;
import java.util.HexFormat;

public class ApiKeyUtil {

    public static String hash(String apiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(apiKey.getBytes());
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash API key", e);
        }
    }
}