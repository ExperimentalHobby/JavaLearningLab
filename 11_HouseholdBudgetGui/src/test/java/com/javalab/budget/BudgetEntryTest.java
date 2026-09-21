package com.javalab.budget;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link BudgetEntry} のCSV形式変換({@link BudgetEntry#toCsvLine()} / {@link BudgetEntry#fromCsvLine(String)})を検証するテスト。
 * 保存・読込がなくアプリを閉じるとデータが消えていた問題への対応として、まずレコード単体の変換を検証する。
 */
class BudgetEntryTest {

    @Test
    void toCsvLineFormatsAllFieldsCommaSeparated() {
        BudgetEntry entry = new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME);

        assertEquals("2026-08-01,給与,300000,INCOME", entry.toCsvLine());
    }

    @Test
    void fromCsvLineParsesAllFields() {
        BudgetEntry entry = BudgetEntry.fromCsvLine("2026-08-01,給与,300000,INCOME");

        assertEquals(LocalDate.of(2026, 8, 1), entry.date());
        assertEquals("給与", entry.category());
        assertEquals(0, new BigDecimal("300000").compareTo(entry.amount()));
        assertEquals(EntryType.INCOME, entry.type());
    }

    @Test
    void fromCsvLineThrowsExceptionForTooFewColumns() {
        assertThrows(BudgetException.class, () -> BudgetEntry.fromCsvLine("2026-08-01,給与"));
    }

    @Test
    void fromCsvLineThrowsExceptionForInvalidDate() {
        assertThrows(BudgetException.class, () -> BudgetEntry.fromCsvLine("not-a-date,給与,300000,INCOME"));
    }

    @Test
    void fromCsvLineThrowsExceptionForInvalidType() {
        assertThrows(BudgetException.class, () -> BudgetEntry.fromCsvLine("2026-08-01,給与,300000,UNKNOWN"));
    }
}
