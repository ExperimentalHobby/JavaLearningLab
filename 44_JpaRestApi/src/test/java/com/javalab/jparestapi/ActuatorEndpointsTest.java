package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@code /actuator/health}・{@code /actuator/metrics}をHTTP経由で結合テストするクラス。
 * 他クラスと共有DBのため、{@code lowStockCount}は絶対値ではなく差分で検証する
 * ({@link LowStockHealthIndicatorTest}と同じ方針)。
 */
@SuppressWarnings("unchecked")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorEndpointsTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductService productService;

    private long fetchLowStockCount() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "/actuator/health", HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
                });
        Map<String, Object> body = Objects.requireNonNull(response.getBody());
        Map<String, Object> components = (Map<String, Object>) body.get("components");
        Map<String, Object> lowStock = (Map<String, Object>) components.get("lowStock");
        Map<String, Object> details = (Map<String, Object>) lowStock.get("details");
        return ((Number) details.get("lowStockCount")).longValue();
    }

    @Test
    void health_returns200WithUpStatus() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        String body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals(true, body.contains("\"status\":\"UP\""));
    }

    @Test
    void health_afterCreatingLowStockProduct_lowStockCountIncreasesByOne() {
        long before = fetchLowStockCount();

        productService.create(new ProductRequest("在庫少ない商品", new BigDecimal("100"), 1));

        long after = fetchLowStockCount();

        assertEquals(before + 1, after);
    }

    @Test
    void metrics_returns200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/metrics", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
