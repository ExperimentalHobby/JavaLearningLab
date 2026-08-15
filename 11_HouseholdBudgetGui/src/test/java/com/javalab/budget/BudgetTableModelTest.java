package com.javalab.budget;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link BudgetTableModel} が{@link BudgetManager}の内容をJTable用の行・列データとして
 * 正しく変換できているかを検証するテスト。Swingの{@link javax.swing.JTable}自体は生成せず、
 * {@link javax.swing.table.AbstractTableModel}のロジック部分だけを対象にしている。
 */
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
        // 列は「日付/カテゴリ/金額/種別」の4列固定であることを確認する。
        assertEquals(4, tableModel.getColumnCount());
    }

    @Test
    void getValueAtReturnsCorrectCellValues() {
        // 列インデックス(0〜3)がそれぞれBudgetEntryのどのフィールドに対応するかを確認する。
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));

        assertEquals(LocalDate.of(2026, 8, 1), tableModel.getValueAt(0, 0));
        assertEquals("給与", tableModel.getValueAt(0, 1));
        assertEquals(new BigDecimal("300000"), tableModel.getValueAt(0, 2));
        assertEquals(EntryType.INCOME, tableModel.getValueAt(0, 3));
    }
}
