package com.javalab.texteditor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link EditorDocument} の状態遷移(現在のファイルパスの保持・更新)を検証するクラス。
 * {@link javax.swing.JFileChooser} 等のGUI要素には依存せず、ドメインロジックのみを検証する。
 */
class EditorDocumentTest {

    @TempDir
    Path tempDir;

    private final EditorDocument document = new EditorDocument(new TextFileService());

    @Test
    void openLoadsContentAndSetsCurrentFile() throws Exception {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "hello", StandardCharsets.UTF_8);

        String content = document.open(file);

        assertEquals("hello", content);
        assertTrue(document.hasCurrentFile());
    }

    @Test
    void hasCurrentFileIsFalseInitially() {
        assertFalse(document.hasCurrentFile());
    }

    @Test
    void saveWritesToCurrentFile() throws Exception {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "original", StandardCharsets.UTF_8);
        document.open(file);

        document.save("updated");

        assertEquals("updated", Files.readString(file, StandardCharsets.UTF_8));
    }

    @Test
    void saveAsSwitchesCurrentFileToNewPath() throws Exception {
        Path original = tempDir.resolve("original.txt");
        Files.writeString(original, "first", StandardCharsets.UTF_8);
        document.open(original);

        Path newPath = tempDir.resolve("renamed.txt");
        document.saveAs(newPath, "second");

        // saveAs後は新しいパスがcurrentFileになるため、saveは新パスへ書き込まれる
        assertEquals("second", Files.readString(newPath, StandardCharsets.UTF_8));
        document.save("third");
        assertEquals("third", Files.readString(newPath, StandardCharsets.UTF_8));
        assertEquals("first", Files.readString(original, StandardCharsets.UTF_8));
    }

    @Test
    void windowTitleReflectsCurrentFile() throws Exception {
        assertEquals("簡易テキストエディタ - 無題", document.windowTitle());

        Path file = tempDir.resolve("report.txt");
        Files.writeString(file, "x", StandardCharsets.UTF_8);
        document.open(file);

        assertEquals("簡易テキストエディタ - report.txt", document.windowTitle());
    }

    @Test
    void isDirtyIsFalseInitially() {
        // 未保存の変更が警告なしに失われる問題への対応。まずdirtyフラグの初期状態を確認する。
        assertFalse(document.isDirty());
    }

    @Test
    void markDirtySetsDirtyFlag() {
        document.markDirty();

        assertTrue(document.isDirty());
    }

    @Test
    void openClearsDirtyFlag() throws Exception {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "hello", StandardCharsets.UTF_8);
        document.markDirty();

        document.open(file);

        assertFalse(document.isDirty());
    }

    @Test
    void saveClearsDirtyFlag() throws Exception {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "hello", StandardCharsets.UTF_8);
        document.open(file);
        document.markDirty();

        document.save("updated");

        assertFalse(document.isDirty());
    }

    @Test
    void saveAsClearsDirtyFlag() throws Exception {
        document.markDirty();

        document.saveAs(tempDir.resolve("new.txt"), "content");

        assertFalse(document.isDirty());
    }

    @Test
    void windowTitleShowsAsteriskWhenDirty() {
        // タイトルバーに変更マーク(*)が出なかった問題への対応。
        document.markDirty();

        assertEquals("簡易テキストエディタ - *無題", document.windowTitle());
    }

    @Test
    void newDocumentResetsCurrentFileAndDirtyFlag() throws Exception {
        // 「新規作成」メニューがなく、一度ファイルを開くと「無題」状態に戻せなかった問題への対応。
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "hello", StandardCharsets.UTF_8);
        document.open(file);
        document.markDirty();

        document.newDocument();

        assertFalse(document.hasCurrentFile());
        assertFalse(document.isDirty());
        assertEquals("簡易テキストエディタ - 無題", document.windowTitle());
    }
}
