package com.javalab.springsecurityauth;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    @Test
    void generateToken_thenExtractUsername_returnsOriginalUsername() {
        JwtService jwtService = new JwtService(Duration.ofMinutes(30));

        String token = jwtService.generateToken("alice");

        assertEquals("alice", jwtService.extractUsername(token));
    }

    @Test
    void extractUsername_expiredToken_throwsJwtException() throws InterruptedException {
        JwtService jwtService = new JwtService(Duration.ofMillis(1));

        String token = jwtService.generateToken("alice");
        Thread.sleep(50);

        assertThrows(JwtException.class, () -> jwtService.extractUsername(token));
    }

    @Test
    void extractUsername_malformedToken_throwsJwtException() {
        JwtService jwtService = new JwtService(Duration.ofMinutes(30));

        assertThrows(JwtException.class, () -> jwtService.extractUsername("not-a-valid-token"));
    }
}
