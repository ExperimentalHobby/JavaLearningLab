package com.javalab.jacksonjsonmapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/** {@link Product}のJSONシリアライズ・デシリアライズを提供する。 */
public final class ProductJsonMapper {

    // JavaTimeModuleのLocalDateSerializerはWRITE_DATES_AS_TIMESTAMPSが有効だと[年,月,日]の配列で
    // 出力してしまうため、無効化してISO-8601形式の文字列("2026-04-01")で出力させる。
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ProductJsonMapper() {
    }

    public static String toJson(Product product) {
        try {
            return MAPPER.writeValueAsString(product);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String toJson(List<Product> products) {
        try {
            return MAPPER.writeValueAsString(products);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Product fromJson(String json) {
        try {
            return MAPPER.readValue(json, Product.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
