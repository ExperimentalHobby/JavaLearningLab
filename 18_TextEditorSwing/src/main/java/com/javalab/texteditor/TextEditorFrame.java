package com.javalab.texteditor;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.nio.file.Path;

/**
 * 簡易テキストエディタのメインウィンドウ。
 * 中央に{@link JTextArea}、上部にファイル操作用の{@link JMenuBar}を配置する。
 * ファイルの状態遷移(現在のファイルパスの保持・更新)は{@link EditorDocument}に委譲し、
 * このクラスはダイアログ操作とエラー表示のみを担当する。
 * GUIの描画・イベント配線自体は自動テスト対象外のため、実際にアプリを起動して手動確認している。
 */
public class TextEditorFrame extends JFrame {

    private final EditorDocument document = new EditorDocument(new TextFileService());
    private final JTextArea textArea = new JTextArea();

    public TextEditorFrame() {
        super("簡易テキストエディタ - 無題");
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        setJMenuBar(buildMenuBar());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    /**
     * 「ファイル」メニュー(開く/保存/名前を付けて保存/終了)を持つメニューバーを構築する。
     * 各項目には{@link java.awt.event.ActionListener}をラムダで登録する(イベントリスナーの実践)。
     */
    private JMenuBar buildMenuBar() {
        JMenu fileMenu = new JMenu("ファイル");

        JMenuItem openItem = new JMenuItem("開く");
        openItem.addActionListener(e -> onOpen());
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("保存");
        saveItem.addActionListener(e -> onSave());
        fileMenu.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("名前を付けて保存");
        saveAsItem.addActionListener(e -> onSaveAs());
        fileMenu.add(saveAsItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("終了");
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        return menuBar;
    }

    private void onOpen() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path path = chooser.getSelectedFile().toPath();
        try {
            textArea.setText(document.open(path));
            setTitle(document.windowTitle());
        } catch (TextFileException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "読み込みエラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSave() {
        if (!document.hasCurrentFile()) {
            onSaveAs();
            return;
        }
        try {
            document.save(textArea.getText());
            setTitle(document.windowTitle());
        } catch (TextFileException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "保存エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSaveAs() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            document.saveAs(chooser.getSelectedFile().toPath(), textArea.getText());
            setTitle(document.windowTitle());
        } catch (TextFileException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "保存エラー", JOptionPane.ERROR_MESSAGE);
        }
    }
}
