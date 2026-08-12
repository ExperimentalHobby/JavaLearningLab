package com.javalab.budget;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BudgetTableModelTest {

    private final BudgetManager manager = new BudgetManager();
    private final BudgetTableModel tableModel = new BudgetTableModel(manager);

    @Test
    void getRowCountReflectsNumberOfEntries() {
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));

        assertEquals(1, tableModel.getRowCount());
    }

    @Test
    void getColumnCountReturnsFour() {
        assertEquals(4, tableModel.getColumnCount());
    }

    @Test
    void getValueAtReturnsCorrectCellValues() {
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));

        assertEquals(LocalDate.of(2026, 8, 1), tableModel.getValueAt(0, 0));
        assertEquals("給与", tableModel.getValueAt(0, 1));
        assertEquals(new BigDecimal("300000"), tableModel.getValueAt(0, 2));
        assertEquals(EntryType.INCOME, tableModel.getValueAt(0, 3));
    }
}
