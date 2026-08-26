package com.rev.g3.i2.ers2.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    // token life span for let say for now 1hr session
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    // the signing and verification key
    private final SecretKey key;

    public JwtService() {
        // loads .env the same way ConnectionFactory does
        Dotenv dotenv = Dotenv.configure().load();

        // read the key defined in our env file
        String secret = dotenv.get("JWT_SECRET");

        // decod that key into base64 format
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        // building a new token
        return Jwts.builder()
                // who this token identifies
                .subject(userDetails.getUsername())
                // timestamp of when the token was created
                .issuedAt(new Date())
                // timestamp of when the token stops being valid
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // sign the token with our secret key signature
                .signWith(key)
                // serialize everything into the final token
                .compact();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                // tell it to verify the signature using our key
                .verifyWith(key)
                // finalize the parser
                .build()
                // parse the token, throws if signature/format is invalid
                .parseSignedClaims(token)
                // get the claims (payload) from the parsed token
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        // return true if the token's expiration date is in the past
        return extractAllClaims(token).getExpiration().before(new Date());

    }

    public String extractUsername(String token) {
        // pull the username
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        // get the username embedded in the token
        String username = extractUsername(token);
        // valid only if it matches the expected user AND isn't expired
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);

    }








}
