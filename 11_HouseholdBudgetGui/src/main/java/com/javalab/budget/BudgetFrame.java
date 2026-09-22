package com.javalab.budget;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * 家計簿アプリのメインウィンドウ。
 * 上部に入力フォーム(種別/カテゴリ/金額/日付 + 追加・更新・削除・保存・読込ボタン)、
 * 中央に取引一覧の{@link JTable}、下部に収入・支出・残高のサマリーを{@link BorderLayout}で配置する。
 * GUIの描画・イベント配線自体は自動テスト対象外のため、実際にアプリを起動して手動確認している。
 */
public class BudgetFrame extends JFrame {

    private final BudgetManager manager = new BudgetManager();
    private final BudgetTableModel tableModel = new BudgetTableModel(manager);
    private final JTable table = new JTable(tableModel);

    private final JComboBox<EntryType> typeCombo = new JComboBox<>(EntryType.values());
    private final JTextField categoryField = new JTextField(10);
    private final JTextField amountField = new JTextField(10);
    private final JTextField dateField = new JTextField(10);

    private final JLabel incomeLabel = new JLabel();
    private final JLabel expenseLabel = new JLabel();
    private final JLabel balanceLabel = new JLabel();

    public BudgetFrame() {
        super("家計簿アプリ");
        setLayout(new BorderLayout());
        add(buildInputPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildSummaryPanel(), BorderLayout.SOUTH);
        refreshSummary();
        resetDateField();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    /**
     * 種別・カテゴリ・金額・日付の入力欄と各種ボタンを持つ上部パネルを構築する。
     * 各ボタンには{@link java.awt.event.ActionListener}をラムダで登録し、
     * クリック時にそれぞれのハンドラメソッドを呼び出す(イベントリスナーの実践)。
     */
    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("種別:"));
        // JComboBoxの既定表示は列挙子名(INCOME/EXPENSE)のままになるため、
        // 日本語表示名(EntryType#getDisplayName())を使うレンダラーを設定する。
        typeCombo.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value == null ? "" : value.getDisplayName()));
        panel.add(typeCombo);
        panel.add(new JLabel("カテゴリ:"));
        panel.add(categoryField);
        panel.add(new JLabel("金額:"));
        panel.add(amountField);
        panel.add(new JLabel("日付(yyyy-MM-dd):"));
        panel.add(dateField);

        JButton addButton = new JButton("追加");
        addButton.addActionListener(e -> onAddButtonClicked());
        panel.add(addButton);

        JButton updateButton = new JButton("更新");
        updateButton.addActionListener(e -> onUpdateButtonClicked());
        panel.add(updateButton);

        JButton deleteButton = new JButton("削除");
        deleteButton.addActionListener(e -> onDeleteButtonClicked());
        panel.add(deleteButton);

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> onSaveButtonClicked());
        panel.add(saveButton);

        JButton loadButton = new JButton("読込");
        loadButton.addActionListener(e -> onLoadButtonClicked());
        panel.add(loadButton);

        return panel;
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.add(incomeLabel);
        panel.add(expenseLabel);
        panel.add(balanceLabel);
        return panel;
    }

    /**
     * 追加ボタンのクリック時に入力内容を読み取り、{@link BudgetManager}にエントリを追加する。
     * 不正な入力(数値変換失敗・日付形式不正・金額0以下・カテゴリ未入力)はダイアログでエラー表示し、
     * ウィンドウを閉じさせない。
     */
    private void onAddButtonClicked() {
        try {
            BudgetEntry entry = readEntryFromFields();
            manager.addEntry(entry);
            afterDataChanged();
            categoryField.setText("");
            amountField.setText("");
            resetDateField();
        } catch (NumberFormatException ex) {
            showError("金額は数値で入力してください");
        } catch (DateTimeParseException ex) {
            showError("日付はyyyy-MM-dd形式で入力してください");
        } catch (BudgetException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * 更新ボタンのクリック時に、テーブルで選択中の行を入力内容で置き換える。
     * 行が選択されていない場合はエラー表示する。
     */
    private void onUpdateButtonClicked() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("更新する行を選択してください");
            return;
        }
        try {
            BudgetEntry entry = readEntryFromFields();
            manager.updateEntry(selectedRow, entry);
            afterDataChanged();
        } catch (NumberFormatException ex) {
            showError("金額は数値で入力してください");
        } catch (DateTimeParseException ex) {
            showError("日付はyyyy-MM-dd形式で入力してください");
        } catch (BudgetException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * 削除ボタンのクリック時に、テーブルで選択中の行を削除する。
     * 行が選択されていない場合はエラー表示する。
     */
    private void onDeleteButtonClicked() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("削除する行を選択してください");
            return;
        }
        manager.removeEntry(selectedRow);
        afterDataChanged();
    }

    /**
     * 保存ボタンのクリック時に{@link JFileChooser}で保存先を選び、CSVファイルへ書き出す。
     */
    private void onSaveButtonClicked() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            manager.saveTo(chooser.getSelectedFile());
            JOptionPane.showMessageDialog(this, "保存しました", "保存", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showError("保存に失敗しました: " + ex.getMessage());
        }
    }

    /**
     * 読込ボタンのクリック時に{@link JFileChooser}で読込元を選び、CSVファイルから読み込む。
     */
    private void onLoadButtonClicked() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            manager.loadFrom(chooser.getSelectedFile());
            afterDataChanged();
        } catch (IOException ex) {
            showError("読込に失敗しました: " + ex.getMessage());
        } catch (BudgetException ex) {
            showError("ファイルの内容が不正です: " + ex.getMessage());
        }
    }

    private BudgetEntry readEntryFromFields() {
        EntryType type = (EntryType) typeCombo.getSelectedItem();
        String category = categoryField.getText().trim();
        BigDecimal amount = new BigDecimal(amountField.getText().trim());
        LocalDate date = LocalDate.parse(dateField.getText().trim());
        return new BudgetEntry(date, category, amount, type);
    }

    private void afterDataChanged() {
        // JTableはTableModelの変更を自動検知しないため、明示的に再描画を通知する。
        tableModel.fireTableDataChanged();
        refreshSummary();
    }

    private void resetDateField() {
        dateField.setText(LocalDate.now().toString());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "入力エラー", JOptionPane.ERROR_MESSAGE);
    }

    private void refreshSummary() {
        incomeLabel.setText("収入: " + manager.getTotalIncome());
        expenseLabel.setText("支出: " + manager.getTotalExpense());
        balanceLabel.setText("残高: " + manager.getBalance());
    }
}
