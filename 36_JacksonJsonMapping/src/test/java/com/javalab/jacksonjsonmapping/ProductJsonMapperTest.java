package com.javalab.jacksonjsonmapping;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductJsonMapperTest {

    @Test
    void toJson_formatsPriceUsingCustomSerializer() {
        Product product = new Product("P001", "ノート", new BigDecimal("150"), LocalDate.of(2026, 4, 1));

        String json = ProductJsonMapper.toJson(product);

        assertTrue(json.contains("\"price\":\"150円\""));
        assertTrue(json.contains("\"releaseDate\":\"2026-04-01\""));
    }

    @Test
    void fromJson_thenToJson_roundTripsToEqualProduct() {
        Product original = new Product("P002", "消しゴム", new BigDecimal("80"), LocalDate.of(2025, 12, 25));

        String json = ProductJsonMapper.toJson(original);
        Product restored = ProductJsonMapper.fromJson(json);

        assertEquals(original, restored);
    }
}
