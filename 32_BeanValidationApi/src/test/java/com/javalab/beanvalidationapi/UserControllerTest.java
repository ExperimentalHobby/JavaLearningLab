package com.javalab.beanvalidationapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link UserController} のREST APIをHTTP経由で結合テストするクラス。
 * {@code webEnvironment = RANDOM_PORT}により実際に組み込みTomcatを起動し、
 * {@link TestRestTemplate}で本物のHTTPリクエストを送る(モック・{@code MockMvc}は使わない)。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void postUsers_validRequest_createsUserAndReturns201() {
        UserRegistrationRequest request = new UserRegistrationRequest("山田太郎", "yamada@example.com", 30);

        ResponseEntity<User> response = restTemplate.postForEntity("/api/users", request, User.class);
        User created = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(created);
        assertEquals("山田太郎", created.name());
        assertEquals("yamada@example.com", created.email());
        assertEquals(30, created.age());
    }

    @Test
    void postUsers_blankName_returns400WithFieldError() {
        UserRegistrationRequest request = new UserRegistrationRequest("", "yamada@example.com", 30);

        ResponseEntity<FieldErrorResponse[]> response = restTemplate.postForEntity("/api/users", request, FieldErrorResponse[].class);
        FieldErrorResponse[] errors = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(errors);
        assertTrue(List.of(errors).stream().anyMatch(e -> e.field().equals("name")));
    }

    @Test
    void postUsers_invalidEmailFormat_returns400WithFieldError() {
        UserRegistrationRequest request = new UserRegistrationRequest("山田太郎", "not-an-email", 30);

        ResponseEntity<FieldErrorResponse[]> response = restTemplate.postForEntity("/api/users", request, FieldErrorResponse[].class);
        FieldErrorResponse[] errors = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(errors);
        assertTrue(List.of(errors).stream().anyMatch(e -> e.field().equals("email")));
    }

    @Test
    void postUsers_negativeAge_returns400WithFieldError() {
        UserRegistrationRequest request = new UserRegistrationRequest("山田太郎", "yamada@example.com", -1);

        ResponseEntity<FieldErrorResponse[]> response = restTemplate.postForEntity("/api/users", request, FieldErrorResponse[].class);
        FieldErrorResponse[] errors = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(errors);
        assertTrue(List.of(errors).stream().anyMatch(e -> e.field().equals("age")));
    }

    @Test
    void getUserById_nonExistentId_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/users/999999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUsers_returnsRegisteredUsers() {
        restTemplate.postForEntity("/api/users", new UserRegistrationRequest("鈴木花子", "suzuki@example.com", 25), User.class);

        ResponseEntity<User[]> response = restTemplate.getForEntity("/api/users", User[].class);
        User[] body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertTrue(List.of(body).stream().anyMatch(u -> u.name().equals("鈴木花子")));
    }
}
