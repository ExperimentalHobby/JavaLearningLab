package com.javalab.budget;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));
        List<BudgetEntry> entries = manager.getEntries();

        assertThrows(UnsupportedOperationException.class, () -> entries.add(
                new BudgetEntry(LocalDate.of(2026, 8, 2), "食費", new BigDecimal("5000"), EntryType.EXPENSE)));
    }
}
