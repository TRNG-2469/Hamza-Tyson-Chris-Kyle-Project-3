package com.rev.g3.i2.ers2.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtService}: token creation, subject extraction, and validity checks
 * (username match + expiry + signature).
 *
 * DESIGN NOTE: {@code JwtService}'s no-arg constructor loads {@code JWT_SECRET} from a {@code .env}
 * file via Dotenv and derives its {@link SecretKey}. That is environment coupling that makes pure
 * unit testing brittle. To keep these tests hermetic we build the instance, then overwrite its
 * private {@code key} field with a known 256-bit test key via {@link ReflectionTestUtils}. This is
 * a seam these tests rely on; the cleaner long-term fix is to inject the secret
 * (e.g. {@code @Value("${jwt.secret}")}) so no reflection is needed.
 *
 * PRECONDITION: A {@code .env} providing a Base64 {@code JWT_SECRET} of >= 32 bytes must exist on the
 * classpath root for {@code new JwtService()} to succeed, OR refactor the constructor to accept the
 * secret. If the constructor throws at load time, these tests error out in setup — that is itself a
 * finding about testability, not a flaky test.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private SecretKey testKey;

    private UserDetails userDetails(String username) {
        return new User(username, "irrelevant", Collections.emptyList());
    }

    @BeforeEach
    void setUp() {
        // A fixed, sufficiently long key so HS256 signing is valid and reproducible across runs.
        byte[] raw = "0123456789-0123456789-0123456789-abc".getBytes(); // >32 bytes
        testKey = Keys.hmacShaKeyFor(raw);

        jwtService = new JwtService();
        // Replace whatever key the Dotenv-based constructor built with our deterministic one.
        ReflectionTestUtils.setField(jwtService, "key", testKey);
    }

    @Test
    void generateToken_producesNonBlankThreePartJwt() {
        String token = jwtService.generateToken(userDetails("alice"));

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length, "a compact JWT has header.payload.signature");
    }

    @Test
    void extractUsername_returnsSubjectThatWasSigned() {
        String token = jwtService.generateToken(userDetails("alice"));

        assertEquals("alice", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValid_true_whenUsernameMatchesAndNotExpired() {
        String token = jwtService.generateToken(userDetails("bob"));

        assertTrue(jwtService.isTokenValid(token, userDetails("bob")));
    }

    @Test
    void isTokenValid_false_whenUsernameDoesNotMatch() {
        String token = jwtService.generateToken(userDetails("bob"));

        assertFalse(jwtService.isTokenValid(token, userDetails("carol")),
                "token subject 'bob' must not validate against user 'carol'");
    }

    @Test
    void isTokenValid_false_forExpiredToken() {
        // Hand-craft a token that already expired, signed with the same test key.
        String expired = Jwts.builder()
                .subject("bob")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(testKey)
                .compact();

        // isTokenExpired() -> extractAllClaims() succeeds (signature ok) then reports expiry,
        // so isTokenValid must return false rather than throw.
        assertFalse(jwtService.isTokenValid(expired, userDetails("bob")));
    }

    @Test
    void extractUsername_throws_whenSignatureKeyDiffers() {
        // Token signed with a DIFFERENT key must fail signature verification on parse.
        SecretKey otherKey = Keys.hmacShaKeyFor("XXXXXXXXXX-XXXXXXXXXX-XXXXXXXXXX-xyz".getBytes());
        String foreignToken = Jwts.builder()
                .subject("mallory")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(otherKey)
                .compact();

        assertThrows(Exception.class, () -> jwtService.extractUsername(foreignToken),
                "parsing a token signed by a foreign key must throw (bad signature)");
    }

    @Test
    void extractUsername_throws_forMalformedToken() {
        assertThrows(Exception.class, () -> jwtService.extractUsername("not-a-jwt"));
    }

    @Test
    void roundTrip_generateThenValidate_holdsForMultipleUsers() {
        for (String name : new String[]{"u1", "user-two", "UPPER"}) {
            String token = jwtService.generateToken(userDetails(name));
            assertEquals(name, jwtService.extractUsername(token));
            assertTrue(jwtService.isTokenValid(token, userDetails(name)));
        }
    }
}
