package com.passport.creditpassport.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    @Value("${spring.security.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private static final String JWT_ISSUER = "credit-passport";

    // Key

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate

    /**
     * Issues a signed JWT.
     *
     * @param userId the entity's UUID `id` field — not natId, not name.
     *               UUIDs are stable; names and national IDs can change.
     *               The filter uses this value to call usersRepository.findById().
     */
    public String generateToken(String userId) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userId)          // subject = UUID primary key
                .issuer(JWT_ISSUER)
                .issuedAt(now)
                .expiration(expiry)
                .id(UUID.randomUUID().toString())
                .signWith(getSigningKey())
                .compact();
    }

    // Validate

    /**
     * Returns true when the token is structurally valid, correctly signed,
     * and not yet expired.
     * The filter catches any exception this might throw, so callers do not
     * need their own try/catch.
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // JwtException subtypes: MalformedJwtException, ExpiredJwtException,
            // SignatureException, UnsupportedJwtException — all mean "not valid"
            return false;
        }
    }

    // Extract

    /**
     * Returns the subject claim — the UUID stored when the token was generated.
     */
    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    // Internal

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}