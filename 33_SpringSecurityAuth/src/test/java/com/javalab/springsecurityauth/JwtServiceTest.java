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

    @Test
    void tokensSignedWithSameConfiguredSecret_canBeVerifiedAcrossInstances() {
        // 署名鍵がインスタンス生成のたびにランダム生成されると、別インスタンス(=アプリ再起動)で
        // 発行済みトークンが検証できなくなる。設定(Base64文字列)から鍵を注入できれば、
        // 別インスタンスでも同じ鍵で検証できるはず。
        String base64Secret = "ZGVtby1zZWNyZXQta2V5LWZvci1qd3Qtc2lnbmluZy1kby1ub3QtdXNlLWluLXByb2Q=";
        JwtService issuer = new JwtService(base64Secret, Duration.ofMinutes(30));
        JwtService verifier = new JwtService(base64Secret, Duration.ofMinutes(30));

        String token = issuer.generateToken("alice");

        assertEquals("alice", verifier.extractUsername(token));
    }
}
