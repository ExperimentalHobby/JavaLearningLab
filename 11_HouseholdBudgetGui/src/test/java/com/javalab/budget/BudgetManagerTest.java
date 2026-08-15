package com.javalab.budget;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link BudgetManager} の集計ロジック(収入合計・支出合計・残高)とバリデーションを検証するテスト。
 * GUI(BudgetFrame)から分離した純粋なロジッククラスのため、Swingの画面を起動せずに
 * 高速にテストできることも設計上のポイント。
 */
class BudgetManagerTest {

    private final BudgetManager manager = new BudgetManager();

    @Test
    void addEntryIncreasesTotalIncomeForIncomeEntry() {
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));

        assertEquals(0, new BigDecimal("300000").compareTo(manager.getTotalIncome()));
    }

    @Test
    void addEntryIncreasesTotalExpenseForExpenseEntry() {
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 2), "食費", new BigDecimal("5000"), EntryType.EXPENSE));

        assertEquals(0, new BigDecimal("5000").compareTo(manager.getTotalExpense()));
    }

    @Test
    void getBalanceReturnsIncomeMinusExpense() {
        // 収入(給与30万)と支出(食費5千)を両方登録し、残高=収入-支出=29万5千になることを確認する。
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 2), "食費", new BigDecimal("5000"), EntryType.EXPENSE));

        assertEquals(0, new BigDecimal("295000").compareTo(manager.getBalance()));
    }

    @Test
    void addEntryThrowsExceptionForZeroOrNegativeAmount() {
        BudgetEntry invalidEntry =
                new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", BigDecimal.ZERO, EntryType.INCOME);

        assertThrows(BudgetException.class, () -> manager.addEntry(invalidEntry));
    }

    @Test
    void getEntriesReturnsUnmodifiableView() {
        // getEntries()が返すListはList.copyOf()によるコピーであり、外部から add() しても
        // BudgetManager内部の状態には影響しない(カプセル化が破られていない)ことを確認する。
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));
        List<BudgetEntry> entries = manager.getEntries();

        assertThrows(UnsupportedOperationException.class, () -> entries.add(
                new BudgetEntry(LocalDate.of(2026, 8, 2), "食費", new BigDecimal("5000"), EntryType.EXPENSE)));
    }
}
