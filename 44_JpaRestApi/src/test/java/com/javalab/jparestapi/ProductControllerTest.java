package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link ProductController} をHTTP経由で結合テストするクラス。
 * {@code webEnvironment = RANDOM_PORT}により実際に組み込みTomcat+実H2へアクセスする
 * (モック・{@code MockMvc}は使わない)。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductService productService;

    @Test
    void postProducts_createsProductAndReturns201() {
        ProductRequest request = new ProductRequest("ノート", new BigDecimal("150"), 100);

        ResponseEntity<ProductResponse> response = restTemplate.postForEntity("/api/products", request, ProductResponse.class);
        ProductResponse created = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(created);
        assertEquals("ノート", created.name());
    }

    @Test
    void getProductById_returnsProductForExistentId() {
        ProductResponse created = productService.create(new ProductRequest("消しゴム", new BigDecimal("80"), 50));

        ResponseEntity<ProductResponse> response = restTemplate.getForEntity("/api/products/" + created.id(), ProductResponse.class);
        ProductResponse body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("消しゴム", body.name());
    }

    @Test
    void getProductById_returns404ForNonExistentId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/products/999999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void putProduct_updatesProduct() {
        ProductResponse created = productService.create(new ProductRequest("ノート", new BigDecimal("150"), 100));
        ProductRequest updateRequest = new ProductRequest("消しゴム", new BigDecimal("80"), 50);

        ResponseEntity<ProductResponse> response = restTemplate.exchange(
                "/api/products/" + created.id(), HttpMethod.PUT,
                new HttpEntity<>(updateRequest), ProductResponse.class);
        ProductResponse updated = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(updated);
        assertEquals("消しゴム", updated.name());
    }

    @Test
    void deleteProduct_removesProductAndReturns204() {
        ProductResponse created = productService.create(new ProductRequest("それから", new BigDecimal("500"), 10));

        ResponseEntity<Void> response =
                restTemplate.exchange("/api/products/" + created.id(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThrows(ProductNotFoundException.class, () -> productService.findById(created.id()));
    }
}
