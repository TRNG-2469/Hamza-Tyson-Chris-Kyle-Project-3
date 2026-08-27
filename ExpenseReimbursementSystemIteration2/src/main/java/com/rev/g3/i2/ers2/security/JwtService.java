package com.rev.g3.i2.ers2.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    // token life span: 1 hour
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    // the signing and verification key
    private SecretKey key;

    /** Spring uses this one; {@code jwt.secret} comes from application.properties / JWT_SECRET. */
    public JwtService(@Value("${jwt.secret:}") String base64Secret) {
        if (base64Secret == null || base64Secret.isBlank()) {
            // No secret configured: fall back to a random key so the app still boots.
            // Tokens will stop validating after a restart, so set JWT_SECRET in .env for real use.
            logger.warn("JWT_SECRET is not set; using a random signing key for this run.");
            this.key = Jwts.SIG.HS256.key().build();
        } else {
            this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        }
    }

    /** Convenience constructor (used by unit tests): random key, no environment required. */
    public JwtService() {
        this("");
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            // expired, malformed, or wrong signature
            return false;
        }
    }
}
