package com.javalab.jparestapi;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/** 商品のレスポンスDTO。JPAエンティティを直接APIに晒さないための境界。 */
public record ProductResponse(
        @Schema(description = "商品ID", example = "1") Long id,
        @Schema(description = "商品名", example = "ノート") String name,
        @Schema(description = "価格(円)", example = "150") BigDecimal price,
        @Schema(description = "在庫数", example = "100") int stock
) {

    static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }
}
