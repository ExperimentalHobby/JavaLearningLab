package com.javalab.texteditor;

import java.nio.file.Path;

/**
 * テキストエディタが開いている1つの文書の状態(現在のファイルパス・未保存の変更の有無)と、
 * 読み込み・保存に伴う状態遷移を担う。{@link javax.swing.JFileChooser} 等のGUI要素には
 * 依存しないため、GUIを起動せずに単体テストできる。
 */
public class EditorDocument {

    private final TextFileService fileService;
    private Path currentFile;
    // 未保存の変更があるかどうか。open/save/saveAs/newDocumentでクリアされ、
    // TextEditorFrame側でテキストが編集されるたびにmarkDirty()が呼ばれる。
    private boolean dirty;

    public EditorDocument(TextFileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 指定パスのファイルを読み込み、現在のファイルとして保持する。未保存の変更フラグはクリアされる。
     * @param path 読み込み対象のパス
     * @return ファイル内容
     */
    public String open(Path path) {
        String content = fileService.load(path);
        currentFile = path;
        dirty = false;
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
     * 未保存の変更フラグはクリアされる。
     * @param content 保存する内容
     */
    public void save(String content) {
        fileService.save(currentFile, content);
        dirty = false;
    }

    /**
     * 指定パスへ内容を保存し、以後の{@link #save(String)}が同じパスへ書き込まれるよう
     * 現在のファイルを切り替える。{@link #save(String)}との違いは、保存先を選び直せる点。
     * 未保存の変更フラグはクリアされる。
     * @param path 保存先のパス
     * @param content 保存する内容
     */
    public void saveAs(Path path, String content) {
        fileService.save(path, content);
        currentFile = path;
        dirty = false;
    }

    /**
     * 現在のファイル・未保存の変更フラグをリセットし、「無題」の新規文書状態に戻す
     * (「新規作成」メニューに対応)。
     */
    public void newDocument() {
        currentFile = null;
        dirty = false;
    }

    /**
     * テキストが編集されたことを通知する。呼び出し側(GUI)がテキスト変更を検知するたびに呼ぶ。
     */
    public void markDirty() {
        dirty = true;
    }

    /**
     * @return 未保存の変更がある場合はtrue
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * ウィンドウタイトルとして表示する文字列を返す。現在のファイルが無い場合は「無題」。
     * 未保存の変更がある場合はファイル名の前に{@code *}を付ける。
     * @return ウィンドウタイトル
     */
    public String windowTitle() {
        String name = currentFile == null ? "無題" : currentFile.getFileName().toString();
        return "簡易テキストエディタ - " + (dirty ? "*" : "") + name;
    }
}
