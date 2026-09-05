package com.javalab.jacksonjsonmapping;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商品カタログの1件分を表す不変な値オブジェクト。
 * @param price 価格。{@link PriceSerializer}/{@link PriceDeserializer}で"1500円"形式の文字列として入出力する
 * @param releaseDate 発売日。{@code jackson-datatype-jsr310}によりISO-8601形式で標準的に入出力する
 */
public record Product(
        String id,
        String name,
        @JsonSerialize(using = PriceSerializer.class) @JsonDeserialize(using = PriceDeserializer.class) BigDecimal price,
        LocalDate releaseDate
) {
}
