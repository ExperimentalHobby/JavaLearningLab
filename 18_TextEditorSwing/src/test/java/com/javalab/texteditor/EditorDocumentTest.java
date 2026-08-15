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
}
