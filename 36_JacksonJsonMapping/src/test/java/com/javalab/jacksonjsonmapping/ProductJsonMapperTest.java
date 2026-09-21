package com.javalab.jacksonjsonmapping;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void fromJson_malformedJson_throwsIllegalArgumentException() {
        // 修正前はUncheckedIOExceptionのままで、Main.runのcatch節をすり抜けてREPLごと落ちていた。
        // ProductXmlMapper.fromXmlと同じ方針(IllegalArgumentException)に揃える。
        assertThrows(IllegalArgumentException.class, () -> ProductJsonMapper.fromJson("{bad"));
    }

    @Test
    void fromJson_wrongShapeJson_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ProductJsonMapper.fromJson("\"not-an-object\""));
    }

    @Test
    void fromJson_nonNumericPrice_throwsIllegalArgumentException() {
        String json = "{\"id\":\"P001\",\"name\":\"ノート\",\"price\":\"abc円\",\"releaseDate\":\"2026-04-01\"}";

        assertThrows(IllegalArgumentException.class, () -> ProductJsonMapper.fromJson(json));
    }

    @Test
    void fromJson_nullPrice_throwsIllegalArgumentException() {
        // 修正前はtext.replace呼び出しでNPEになっていた。
        String json = "{\"id\":\"P001\",\"name\":\"ノート\",\"price\":null,\"releaseDate\":\"2026-04-01\"}";

        assertThrows(IllegalArgumentException.class, () -> ProductJsonMapper.fromJson(json));
    }
}
