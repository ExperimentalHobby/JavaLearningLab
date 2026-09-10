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

/** {@link OrderController} をHTTP経由で結合テストするクラス。 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductService productService;

    @Test
    void postOrders_sufficientStock_returns200() {
        ProductResponse pen = productService.create(new ProductRequest("ペン", new BigDecimal("100"), 10));

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/api/orders", List.of(new OrderLine(pen.id(), 3)), Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(7, productService.findById(pen.id()).stock());
    }

    @Test
    void postOrders_insufficientStock_returns409() {
        ProductResponse pen = productService.create(new ProductRequest("ペン", new BigDecimal("100"), 1));

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/orders", List.of(new OrderLine(pen.id(), 5)), String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
