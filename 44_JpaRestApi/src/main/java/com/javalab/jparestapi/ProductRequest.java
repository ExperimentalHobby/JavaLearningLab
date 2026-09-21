package com.javalab.jparestapi;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** 商品の登録・更新リクエストDTO。JPAエンティティを直接APIに晒さないための境界。 */
public record ProductRequest(
        @NotBlank(message = "商品名は必須です") @Schema(description = "商品名", example = "ノート") String name,
        @PositiveOrZero(message = "価格は0以上である必要があります") @Schema(description = "価格(円)", example = "150") BigDecimal price,
        @PositiveOrZero(message = "在庫数は0以上である必要があります") @Schema(description = "在庫数", example = "100") int stock
) {
}
