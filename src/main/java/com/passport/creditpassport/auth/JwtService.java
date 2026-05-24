package com.passport.creditpassport.auth;

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

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Issues a signed JWT.
     *
     * @param userId the entity's UUID `id` field — not natId, not name.
     *               UUIDs are stable; names and national IDs can change.
     *               The filter uses this value to call usersRepository.findById().
     */
    public String generateToken(String userId, String natId, String name, String number, String email) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userId)
                .claim("natId", natId)
                .claim("name", name)
                .claim("number", number)
                .claim("email", email)
                .issuer(JWT_ISSUER)
                .issuedAt(now)
                .expiration(expiry)
                .id(UUID.randomUUID().toString())
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Returns true when the token is structurally valid, correctly signed,
     * and not yet expired. The filter catches any exception this might throw,
     * so callers do not need their own try/catch.
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the subject claim — the UUID stored when the token was generated.
     */
    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractNatId(String token) {
        return extractClaims(token).get("natId", String.class);
    }
    public String extractName(String token) {
        return extractClaims(token).get("name", String.class);
    }
    public String extractNumber(String token) {
        return extractClaims(token).get("number", String.class);
    }
    public String extractEmail(String token) {
        return extractClaims(token).get("email", String.class);
    }
}
