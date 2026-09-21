package com.javalab.budget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * 家計簿の1件(収入または支出)を表すイミュータブルな値オブジェクト。
 * @param date 発生日
 * @param category カテゴリ(例: "給与", "食費")
 * @param amount 金額
 * @param type 収入/支出の種別
 */
public record BudgetEntry(LocalDate date, String category, BigDecimal amount, EntryType type) {

    /**
     * CSVの1行形式に変換する(例: {@code "2026-08-01,給与,300000,INCOME"})。
     * @return CSV形式の文字列
     */
    public String toCsvLine() {
        return date + "," + category + "," + amount + "," + type;
    }

    /**
     * {@link #toCsvLine()} で書き出した形式の1行からBudgetEntryを復元する。
     * @param line CSVファイルから読み込んだ1行
     * @return 復元されたBudgetEntry
     * @throws BudgetException lineのカラム数が不足している、または日付・金額・種別の形式が不正な場合
     *         (手編集等で壊れたCSVをloadしてもクラッシュしないようにするため)
     */
    public static BudgetEntry fromCsvLine(String line) {
        String[] parts = line.split(",", 4);
        if (parts.length != 4) {
            throw new BudgetException("CSVの行形式が不正です: " + line);
        }
        try {
            LocalDate parsedDate = LocalDate.parse(parts[0]);
            String parsedCategory = parts[1];
            BigDecimal parsedAmount = new BigDecimal(parts[2]);
            EntryType parsedType = EntryType.valueOf(parts[3]);
            return new BudgetEntry(parsedDate, parsedCategory, parsedAmount, parsedType);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            // NumberFormatException(金額)・IllegalArgumentException(種別)・
            // DateTimeParseException(日付)をまとめてBudgetExceptionに変換する。
            throw new BudgetException("CSVの行形式が不正です: " + line);
        }
    }
}
