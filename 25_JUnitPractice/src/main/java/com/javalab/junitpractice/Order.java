package com.javalab.junitpractice;

import java.math.BigDecimal;

/**
 * 注文1件を表す不変な値オブジェクト。
 * @param id 注文ID
 * @param customerEmail 注文者のメールアドレス
 * @param total 合計金額
 */
public record Order(long id, String customerEmail, BigDecimal total) {
}
