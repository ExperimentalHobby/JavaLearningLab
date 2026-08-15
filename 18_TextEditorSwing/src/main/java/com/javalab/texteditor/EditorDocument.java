package com.javalab.texteditor;

import java.nio.file.Path;

/**
 * テキストエディタが開いている1つの文書の状態(現在のファイルパス)と、
 * 読み込み・保存に伴う状態遷移を担う。{@link javax.swing.JFileChooser} 等のGUI要素には
 * 依存しないため、GUIを起動せずに単体テストできる。
 */
public class EditorDocument {

    private final TextFileService fileService;
    private Path currentFile;

    public EditorDocument(TextFileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 指定パスのファイルを読み込み、現在のファイルとして保持する。
     * @param path 読み込み対象のパス
     * @return ファイル内容
     */
    public String open(Path path) {
        String content = fileService.load(path);
        currentFile = path;
        return content;
    }

    /**
     * @return 現在のファイルが設定されている場合はtrue
     */
    public boolean hasCurrentFile() {
        return currentFile != null;
    }

    /**
     * 現在のファイルへ内容を保存する。{@link #hasCurrentFile()} がfalseの状態で
     * 呼び出す想定はなく、呼び出し側で {@link #saveAs(Path, String)} に振り分ける。
     * @param content 保存する内容
     */
    public void save(String content) {
        fileService.save(currentFile, content);
    }

    /**
     * 指定パスへ内容を保存し、以後の{@link #save(String)}が同じパスへ書き込まれるよう
     * 現在のファイルを切り替える。{@link #save(String)}との違いは、保存先を選び直せる点。
     * @param path 保存先のパス
     * @param content 保存する内容
     */
    public void saveAs(Path path, String content) {
        fileService.save(path, content);
        currentFile = path;
    }

    /**
     * ウィンドウタイトルとして表示する文字列を返す。現在のファイルが無い場合は「無題」。
     * @return ウィンドウタイトル
     */
    public String windowTitle() {
        return currentFile == null
                ? "簡易テキストエディタ - 無題"
                : "簡易テキストエディタ - " + currentFile.getFileName();
    }
}
