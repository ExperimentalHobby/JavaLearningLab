package com.javalab.springsecurityauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link AuthController} をHTTP経由で結合テストするクラス。
 * {@code webEnvironment = RANDOM_PORT}により実際に組み込みTomcatを起動し、
 * {@link TestRestTemplate}で本物のHTTPリクエストを送る(モックは使わない)。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void login_correctCredentials_returns200WithToken() {
        AuthRequest request = new AuthRequest("alice", "password");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/api/auth/login", request, AuthResponse.class);
        AuthResponse body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertFalse(body.token().isBlank());
    }

    @Test
    void login_wrongPassword_returns401WithJapaneseMessageBody() {
        AuthRequest request = new AuthRequest("alice", "wrong-password");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/auth/login", request, ErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(new ErrorResponse("認証に失敗しました"), response.getBody());
    }

    @Test
    void login_unknownUsername_returns401() {
        AuthRequest request = new AuthRequest("unknown-user", "password");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void login_repeatedFailuresThenCorrectPassword_staysLockedOut() {
        // ブルートフォース対策として、規定回数(5回)失敗すると正しいパスワードでもロックアウトされる。
        AuthRequest wrongPassword = new AuthRequest("lockout-target", "wrong-password");
        for (int i = 0; i < 5; i++) {
            restTemplate.postForEntity("/api/auth/login", wrongPassword, String.class);
        }

        AuthRequest unknownButCorrectShapePassword = new AuthRequest("lockout-target", "password");
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", unknownButCorrectShapePassword, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
