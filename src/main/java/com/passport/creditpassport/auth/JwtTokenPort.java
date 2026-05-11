package com.passport.creditpassport.auth;

public interface JwtTokenPort {
    boolean validateToken(String token);
    String extractSubject(String token);
}
