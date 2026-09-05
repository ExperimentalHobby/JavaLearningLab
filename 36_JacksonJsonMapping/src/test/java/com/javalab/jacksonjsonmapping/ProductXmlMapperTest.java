package com.javalab.jacksonjsonmapping;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductXmlMapperTest {

    @Test
    void toXml_wrapsProductsInRootElement() {
        List<Product> products = List.of(
                new Product("P001", "ノート", new BigDecimal("150"), LocalDate.of(2026, 4, 1)));

        String xml = ProductXmlMapper.toXml(products);

        assertTrue(xml.contains("<products>"));
        assertTrue(xml.contains("<product>"));
        assertTrue(xml.contains("<price>150円</price>"));
    }

    @Test
    void fromXml_thenToXml_roundTripsToEqualProduct() {
        Product original = new Product("P002", "消しゴム", new BigDecimal("80"), LocalDate.of(2025, 12, 25));
        String xml = ProductXmlMapper.toXml(original);

        Product restored = ProductXmlMapper.fromXml(xml);

        assertEquals(original, restored);
    }
}
