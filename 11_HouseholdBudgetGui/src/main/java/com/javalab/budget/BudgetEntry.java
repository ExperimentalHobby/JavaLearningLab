package com.javalab.budget;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 家計簿の1件(収入または支出)を表すイミュータブルな値オブジェクト。
 * @param date 発生日
 * @param category カテゴリ(例: "給与", "食費")
 * @param amount 金額
 * @param type 収入/支出の種別
 */
public record BudgetEntry(LocalDate date, String category, BigDecimal amount, EntryType type) {
}
