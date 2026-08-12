package com.javalab.budget;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 家計簿アプリのメインウィンドウ。
 * 上部に入力フォーム(種別/カテゴリ/金額 + 追加ボタン)、中央に取引一覧の{@link JTable}、
 * 下部に収入・支出・残高のサマリーを{@link BorderLayout}で配置する。
 * GUIの描画・イベント配線自体は自動テスト対象外のため、実際にアプリを起動して手動確認している。
 */
public class BudgetFrame extends JFrame {

    private final BudgetManager manager = new BudgetManager();
    private final BudgetTableModel tableModel = new BudgetTableModel(manager);

    private final JComboBox<EntryType> typeCombo = new JComboBox<>(EntryType.values());
    private final JTextField categoryField = new JTextField(10);
    private final JTextField amountField = new JTextField(10);

    private final JLabel incomeLabel = new JLabel();
    private final JLabel expenseLabel = new JLabel();
    private final JLabel balanceLabel = new JLabel();

    public BudgetFrame() {
        super("家計簿アプリ");
        setLayout(new BorderLayout());
        add(buildInputPanel(), BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        add(buildSummaryPanel(), BorderLayout.SOUTH);
        refreshSummary();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    /**
     * 種別・カテゴリ・金額の入力欄と追加ボタンを持つ上部パネルを構築する。
     * 追加ボタンには{@link java.awt.event.ActionListener}をラムダで登録し、
     * クリック時に{@link #onAddButtonClicked()}を呼び出す(イベントリスナーの実践)。
     */
    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("種別:"));
        panel.add(typeCombo);
        panel.add(new JLabel("カテゴリ:"));
        panel.add(categoryField);
        panel.add(new JLabel("金額:"));
        panel.add(amountField);

        JButton addButton = new JButton("追加");
        addButton.addActionListener(e -> onAddButtonClicked());
        panel.add(addButton);

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
     * 不正な入力(数値変換失敗・金額0以下)はダイアログでエラー表示し、ウィンドウを閉じさせない。
     */
    private void onAddButtonClicked() {
        try {
            EntryType type = (EntryType) typeCombo.getSelectedItem();
            String category = categoryField.getText().trim();
            BigDecimal amount = new BigDecimal(amountField.getText().trim());

            manager.addEntry(new BudgetEntry(LocalDate.now(), category, amount, type));

            // JTableはTableModelの変更を自動検知しないため、明示的に再描画を通知する。
            tableModel.fireTableDataChanged();
            refreshSummary();
            categoryField.setText("");
            amountField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "金額は数値で入力してください", "入力エラー", JOptionPane.ERROR_MESSAGE);
        } catch (BudgetException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "入力エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshSummary() {
        incomeLabel.setText("収入: " + manager.getTotalIncome());
        expenseLabel.setText("支出: " + manager.getTotalExpense());
        balanceLabel.setText("残高: " + manager.getBalance());
    }
}
