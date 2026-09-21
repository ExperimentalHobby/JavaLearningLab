package com.javalab.texteditor;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 簡易テキストエディタのメインウィンドウ。
 * 中央に{@link JTextArea}、上部にファイル操作用の{@link JMenuBar}を配置する。
 * ファイルの状態遷移(現在のファイルパス・未保存の変更の保持・更新)は{@link EditorDocument}に委譲し、
 * このクラスはダイアログ操作とエラー表示のみを担当する。
 * GUIの描画・イベント配線自体は自動テスト対象外のため、実際にアプリを起動して手動確認している。
 */
public class TextEditorFrame extends JFrame {

    private final EditorDocument document = new EditorDocument(new TextFileService());
    private final JTextArea textArea = new JTextArea();
    private final UndoManager undoManager = new UndoManager();

    public TextEditorFrame() {
        super("簡易テキストエディタ - 無題");
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        setJMenuBar(buildMenuBar());
        wireTextAreaListeners();

        // ウィンドウを閉じる操作の前に未保存の変更を確認したいため、既定のEXIT_ON_CLOSEではなく
        // DO_NOTHING_ON_CLOSEにし、WindowListenerで確認してから自前でdispose+終了する。
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (confirmDiscardUnsavedChanges("終了しますか?")) {
                    dispose();
                    System.exit(0);
                }
            }
        });
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    /**
     * テキスト変更検知(dirty管理)とUndo/Redo履歴の記録を{@link JTextArea}へ配線する。
     */
    private void wireTextAreaListeners() {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onTextChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onTextChanged();
            }
        });
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
    }

    private void onTextChanged() {
        document.markDirty();
        setTitle(document.windowTitle());
    }

    /**
     * 「ファイル」「編集」メニューを持つメニューバーを構築する。
     * 各項目には{@link java.awt.event.ActionListener}をラムダで登録する(イベントリスナーの実践)。
     */
    private JMenuBar buildMenuBar() {
        JMenu fileMenu = new JMenu("ファイル");

        JMenuItem newItem = new JMenuItem("新規作成");
        newItem.addActionListener(e -> onNew());
        fileMenu.add(newItem);

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
        exitItem.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("編集");
        JMenuItem undoItem = new JMenuItem("元に戻す");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        undoItem.addActionListener(e -> onUndo());
        editMenu.add(undoItem);

        JMenuItem redoItem = new JMenuItem("やり直す");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        redoItem.addActionListener(e -> onRedo());
        editMenu.add(redoItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
    }

    private void onNew() {
        if (!confirmDiscardUnsavedChanges("未保存の変更を破棄して新規作成しますか?")) {
            return;
        }
        textArea.setText("");
        document.newDocument();
        undoManager.discardAllEdits();
        setTitle(document.windowTitle());
    }

    private void onOpen() {
        if (!confirmDiscardUnsavedChanges("未保存の変更を破棄してファイルを開きますか?")) {
            return;
        }
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path path = chooser.getSelectedFile().toPath();
        try {
            textArea.setText(document.open(path));
            undoManager.discardAllEdits();
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
        File selected = chooser.getSelectedFile();
        // JFileChooserの既定は上書き確認をしないため、既存ファイルが選ばれた場合は
        // 明示的に確認ダイアログを表示してから保存する。
        if (Files.exists(selected.toPath())) {
            int result = JOptionPane.showConfirmDialog(
                    this, selected.getName() + " は既に存在します。上書きしますか?",
                    "上書き確認", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        try {
            document.saveAs(selected.toPath(), textArea.getText());
            setTitle(document.windowTitle());
        } catch (TextFileException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "保存エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUndo() {
        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (CannotUndoException ex) {
            // これ以上戻せない場合は何もしない(ボタン連打等で起こりうる無害なケース)。
        }
    }

    private void onRedo() {
        try {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (CannotRedoException ex) {
            // これ以上やり直せない場合は何もしない。
        }
    }

    /**
     * 未保存の変更がある場合のみ確認ダイアログを表示する。変更がない、または「はい」が選ばれた場合はtrue。
     * @param message 確認ダイアログに表示するメッセージ
     * @return 処理を続行してよい場合true
     */
    private boolean confirmDiscardUnsavedChanges(String message) {
        if (!document.isDirty()) {
            return true;
        }
        int result = JOptionPane.showConfirmDialog(
                this, message, "未保存の変更があります", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
