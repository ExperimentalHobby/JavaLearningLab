package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** {@link OrderController} をHTTP経由で結合テストするクラス。 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductService productService;

    @Test
    void postOrders_sufficientStock_returns200WithUpdatedStock() {
        ProductResponse pen = productService.create(new ProductRequest("ペン", new BigDecimal("100"), 10));

        ResponseEntity<ProductResponse[]> response = restTemplate.postForEntity(
                "/api/orders", List.of(new OrderLine(pen.id(), 3)), ProductResponse[].class);
        ProductResponse[] results = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(results);
        assertEquals(1, results.length);
        assertEquals(7, results[0].stock());
        assertEquals(7, productService.findById(pen.id()).stock());
    }

    @Test
    void postOrders_insufficientStock_returns409() {
        ProductResponse pen = productService.create(new ProductRequest("ペン", new BigDecimal("100"), 1));

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/orders", List.of(new OrderLine(pen.id(), 5)), String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void postOrders_negativeQuantity_returns400WithErrorMessage() {
        ProductResponse pen = productService.create(new ProductRequest("ペン", new BigDecimal("100"), 10));

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/api/orders", List.of(new OrderLine(pen.id(), -5)), ErrorResponse.class);
        ErrorResponse error = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("数量"));
        // 在庫はバリデーションで弾かれ変化していないはず(quantityが負でも在庫が増えてしまう不具合の対応)。
        assertEquals(10, productService.findById(pen.id()).stock());
    }

    @Test
    void postOrders_nullProductId_returns400WithErrorMessage() {
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/api/orders", List.of(new OrderLine(null, 3)), ErrorResponse.class);
        ErrorResponse error = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("商品ID"));
    }
}
