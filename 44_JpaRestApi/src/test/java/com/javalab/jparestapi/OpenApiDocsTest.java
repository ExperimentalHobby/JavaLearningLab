package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** springdoc-openapiによるOpenAPI仕様・Swagger UIの自動生成を結合テストで検証する。 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiDocsTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void apiDocs_returns200WithProductsPath() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        String body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertTrue(body.contains("/api/products"));
    }

    @Test
    void swaggerUi_returns200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/swagger-ui/index.html", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void apiDocs_containsCustomizedTitle() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        String body = response.getBody();

        assertNotNull(body);
        assertTrue(body.contains("商品在庫管理API"));
    }
}
