package com.javalab.streamapi;

import java.math.BigDecimal;

/**
 * 1件の売上を表すイミュータブルな値オブジェクト。
 * @param product 商品名
 * @param category カテゴリ
 * @param amount 金額
 * @param quantity 数量
 */
public record SalesRecord(String product, String category, BigDecimal amount, int quantity) {

    /**
     * @throws IllegalArgumentException amountまたはquantityが負の値の場合
     */
    public SalesRecord {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金額は負の値にできません: " + amount);
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("数量は負の値にできません: " + quantity);
        }
    }
}
