package com.javalab.budget;

import javax.swing.table.AbstractTableModel;

/**
 * {@link BudgetManager} が保持するエントリを{@link javax.swing.JTable}に表示するためのモデル。
 * 画面描画を伴わない{@link AbstractTableModel}のロジックのみなので、Swingの表示なしにテストできる。
 */
public class BudgetTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"日付", "カテゴリ", "金額", "種別"};

    private final BudgetManager manager;

    public BudgetTableModel(BudgetManager manager) {
        this.manager = manager;
    }

    @Override
    public int getRowCount() {
        return manager.getEntries().size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BudgetEntry entry = manager.getEntries().get(rowIndex);
        return switch (columnIndex) {
            case 0 -> entry.date();
            case 1 -> entry.category();
            case 2 -> entry.amount();
            case 3 -> entry.type();
            default -> throw new IllegalArgumentException("不正な列番号です: " + columnIndex);
        };
    }
}
