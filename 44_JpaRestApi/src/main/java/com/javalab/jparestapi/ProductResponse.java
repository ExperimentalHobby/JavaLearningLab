package com.javalab.jparestapi;

import java.math.BigDecimal;

/** 商品のレスポンスDTO。JPAエンティティを直接APIに晒さないための境界。 */
public record ProductResponse(Long id, String name, BigDecimal price, int stock) {

    static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }
}
