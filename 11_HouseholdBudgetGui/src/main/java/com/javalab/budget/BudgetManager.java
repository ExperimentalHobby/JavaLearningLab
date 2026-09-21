package com.javalab.budget;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
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
     * @throws BudgetException entryの金額が0以下、またはカテゴリが空文字・空白のみの場合
     */
    public void addEntry(BudgetEntry entry) {
        validateEntry(entry);
        entries.add(entry);
    }

    private static void validateEntry(BudgetEntry entry) {
        if (entry.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BudgetException("金額は0より大きい必要があります: " + entry.amount());
        }
        if (entry.category().isBlank()) {
            throw new BudgetException("カテゴリを入力してください");
        }
    }

    /**
     * 指定インデックスのエントリを削除する。
     * @param index 削除対象のインデックス
     * @throws BudgetException indexが範囲外の場合
     */
    public void removeEntry(int index) {
        validateIndex(index);
        entries.remove(index);
    }

    /**
     * 指定インデックスのエントリを新しい内容で置き換える。
     * @param index 更新対象のインデックス
     * @param newEntry 新しい内容のエントリ
     * @throws BudgetException indexが範囲外、またはnewEntryが不正な場合(金額0以下・カテゴリ空白)
     */
    public void updateEntry(int index, BudgetEntry newEntry) {
        validateIndex(index);
        validateEntry(newEntry);
        entries.set(index, newEntry);
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= entries.size()) {
            throw new BudgetException("不正な行番号です: " + index);
        }
    }

    /**
     * @return 登録済みエントリの変更不可なビュー。{@code List.copyOf}のような全件コピーは行わず、
     *         {@link Collections#unmodifiableList}で内部リストをラップするだけのO(1)の参照専用ビューを返す。
     *         これにより{@link BudgetTableModel}がセル描画のたびに呼んでも重くならない。
     */
    public List<BudgetEntry> getEntries() {
        return Collections.unmodifiableList(entries);
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

    /**
     * 現在のエントリ一覧をCSVファイルに書き出す(1行1エントリ、{@link BudgetEntry#toCsvLine()}形式)。
     * @param file 書き込み先ファイル
     * @throws IOException 書き込みに失敗した場合
     */
    public void saveTo(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            for (BudgetEntry entry : entries) {
                writer.write(entry.toCsvLine());
                writer.newLine();
            }
        }
    }

    /**
     * CSVファイルからエントリ一覧を読み込み、現在のエントリを置き換える。
     * @param file 読み込み元ファイル
     * @throws IOException 読み込みに失敗した場合
     */
    public void loadFrom(File file) throws IOException {
        // 読込前にクリアすることで、load後の状態がファイル内容と完全に一致するようにする。
        entries.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                entries.add(BudgetEntry.fromCsvLine(line));
            }
        }
    }
}
