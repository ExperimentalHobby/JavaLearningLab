package com.javalab.springsecurityauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link SecureController} をHTTP経由で結合テストするクラス。JWTの発行は{@code AuthController}経由で行う。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecureControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String issueToken() {
        AuthResponse response = restTemplate.postForObject(
                "/api/auth/login", new AuthRequest("alice", "password"), AuthResponse.class);
        return response.token();
    }

    @Test
    void hello_withoutAuthorizationHeader_returns401() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/secure/hello", String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void hello_withValidToken_returns200WithGreeting() {
        String token = issueToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/secure/hello", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello, alice", response.getBody());
    }

    @Test
    void hello_withInvalidToken_returns401() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("not-a-valid-token");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/secure/hello", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
