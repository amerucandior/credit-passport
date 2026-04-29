package com.passport.creditpassport.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static java.security.KeyRep.Type.SECRET;

@Service
public class JwtService {

    private static final String JWT_SECRET = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";
    private static final String JWT_ISSUER = "credit-passport";
    private static final long JWT_EXPIRATION_TIME = 900000; //15 mins

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    public String generateToken(String userName) {
        Date now = new Date();

        Date expiry = new Date(now.getTime() + JWT_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(userName)
                .issuer(JWT_ISSUER)
                .issuedAt(now)
                .expiration(expiry)
                .id(UUID.randomUUID().toString())
                .signWith(getSigningKey())
                .compact();

    }

    // extract username from token
    public String extractUserName(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    // Check token has not expired
    public boolean validateToken(String token, String userName) {
        return extractUserName(token).equals(userName)
                && !extractExpiration(token).before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
