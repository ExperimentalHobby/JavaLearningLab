package com.javalab.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 家計簿の収支を管理する。GUI(BudgetFrame)から分離した純粋なロジックとして実装することで、
 * 画面描画なしにテストできるようにしている。
 */
public class BudgetManager {

    private final List<BudgetEntry> entries = new ArrayList<>();

    /**
     * エントリを追加する。
     * @param entry 追加するエントリ
     * @throws BudgetException entryの金額が0以下の場合
     */
    public void addEntry(BudgetEntry entry) {
        if (entry.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BudgetException("金額は0より大きい必要があります: " + entry.amount());
        }
        entries.add(entry);
    }

    /**
     * @return 登録済みエントリの変更不可なビュー
     */
    public List<BudgetEntry> getEntries() {
        return List.copyOf(entries);
    }

    /**
     * @return 収入エントリの合計金額
     */
    public BigDecimal getTotalIncome() {
        return totalOf(EntryType.INCOME);
    }

    /**
     * @return 支出エントリの合計金額
     */
    public BigDecimal getTotalExpense() {
        return totalOf(EntryType.EXPENSE);
    }

    /**
     * @return 収支残高(収入合計 - 支出合計)
     */
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }

    private BigDecimal totalOf(EntryType type) {
        BigDecimal total = BigDecimal.ZERO;
        for (BudgetEntry entry : entries) {
            if (entry.type() == type) {
                total = total.add(entry.amount());
            }
        }
        return total;
    }
}
