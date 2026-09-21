package com.javalab.budget;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link EntryType#getDisplayName()} の日本語表示名を検証するテスト。
 * {@code JComboBox<EntryType>}が英語の列挙子名(INCOME/EXPENSE)のまま表示されていた問題への対応。
 */
class EntryTypeTest {

    @Test
    void incomeDisplayNameIsJapanese() {
        assertEquals("収入", EntryType.INCOME.getDisplayName());
    }

    @Test
    void expenseDisplayNameIsJapanese() {
        assertEquals("支出", EntryType.EXPENSE.getDisplayName());
    }
}
