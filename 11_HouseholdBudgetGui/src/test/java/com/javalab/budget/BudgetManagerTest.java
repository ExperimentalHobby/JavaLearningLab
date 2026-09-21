package com.javalab.budget;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
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

    @TempDir
    Path tempDir;

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
    void addEntryThrowsExceptionForBlankCategory() {
        // addEntryは金額しか検証しておらず、カテゴリが空文字でも登録できてしまっていた。
        BudgetEntry blankCategoryEntry =
                new BudgetEntry(LocalDate.of(2026, 8, 1), "  ", new BigDecimal("1000"), EntryType.INCOME);

        assertThrows(BudgetException.class, () -> manager.addEntry(blankCategoryEntry));
    }

    @Test
    void removeEntryDeletesEntryAtIndex() {
        // 登録した行の削除ができなかった問題への対応。
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 2), "食費", new BigDecimal("5000"), EntryType.EXPENSE));

        manager.removeEntry(0);

        assertEquals(1, manager.getEntries().size());
        assertEquals("食費", manager.getEntries().get(0).category());
    }

    @Test
    void removeEntryThrowsExceptionForInvalidIndex() {
        assertThrows(BudgetException.class, () -> manager.removeEntry(0));
    }

    @Test
    void updateEntryReplacesEntryAtIndex() {
        // 登録した行の編集ができなかった問題への対応。
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));
        BudgetEntry updated = new BudgetEntry(LocalDate.of(2026, 8, 1), "賞与", new BigDecimal("500000"), EntryType.INCOME);

        manager.updateEntry(0, updated);

        assertEquals("賞与", manager.getEntries().get(0).category());
        assertEquals(0, new BigDecimal("500000").compareTo(manager.getEntries().get(0).amount()));
    }

    @Test
    void updateEntryThrowsExceptionForInvalidIndex() {
        BudgetEntry entry = new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME);

        assertThrows(BudgetException.class, () -> manager.updateEntry(0, entry));
    }

    @Test
    void updateEntryThrowsExceptionForInvalidNewEntry() {
        // 更新後の内容も追加時と同じバリデーション(金額・カテゴリ)を通ることを確認する。
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));
        BudgetEntry invalidEntry =
                new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", BigDecimal.ZERO, EntryType.INCOME);

        assertThrows(BudgetException.class, () -> manager.updateEntry(0, invalidEntry));
    }

    @Test
    void saveToAndLoadFromRoundTripsEntries() throws IOException {
        // 保存・読込がなくアプリを閉じるとデータが消えていた問題への対応。
        // 収入・支出が混在した状態で保存し、別のBudgetManagerへ読み込んだ結果が
        // 元の内容と完全に一致することを確認する「ラウンドトリップテスト」。
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 1), "給与", new BigDecimal("300000"), EntryType.INCOME));
        manager.addEntry(new BudgetEntry(LocalDate.of(2026, 8, 2), "食費", new BigDecimal("5000"), EntryType.EXPENSE));
        File file = tempDir.resolve("budget.csv").toFile();

        manager.saveTo(file);
        BudgetManager loaded = new BudgetManager();
        loaded.loadFrom(file);

        assertEquals(2, loaded.getEntries().size());
        assertEquals("給与", loaded.getEntries().get(0).category());
        assertEquals(EntryType.INCOME, loaded.getEntries().get(0).type());
        assertEquals("食費", loaded.getEntries().get(1).category());
        assertEquals(EntryType.EXPENSE, loaded.getEntries().get(1).type());
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
