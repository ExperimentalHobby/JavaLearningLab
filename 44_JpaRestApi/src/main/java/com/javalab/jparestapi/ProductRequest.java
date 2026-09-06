package com.javalab.jparestapi;

import java.math.BigDecimal;

/** 商品の登録・更新リクエストDTO。JPAエンティティを直接APIに晒さないための境界。 */
public record ProductRequest(String name, BigDecimal price, int stock) {
}
