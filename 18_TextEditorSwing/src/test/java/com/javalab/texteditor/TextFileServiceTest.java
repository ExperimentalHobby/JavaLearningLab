package com.javalab.texteditor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link TextFileService} のファイル読み書きロジックを検証するテスト。
 * GUI(TextEditorFrame)から分離した純粋なロジックのため、Swingの画面を起動せず
 * {@code @TempDir}が提供する実際の一時ディレクトリに対してテストできる。
 */
class TextFileServiceTest {

    private final TextFileService service = new TextFileService();

    @TempDir
    Path tempDir;

    @Test
    void saveThenLoadReturnsSameContent() {
        // 改行を含む複数行のテキストを保存してから読み込み直し、内容が完全に一致する
        // ことを確認する「ラウンドトリップテスト」。
        Path file = tempDir.resolve("memo.txt");
        String content = "こんにちは\n2行目";

        service.save(file, content);
        String loaded = service.load(file);

        assertEquals(content, loaded);
    }

    @Test
    void loadThrowsTextFileExceptionForNonExistentFile() {
        Path file = tempDir.resolve("does-not-exist.txt");

        assertThrows(TextFileException.class, () -> service.load(file));
    }

    @Test
    void saveThrowsTextFileExceptionForNonExistentDirectory() {
        // 親ディレクトリ("no-such-dir")が存在しない場合、Files.writeString()は
        // 自動でディレクトリを作成せずIOExceptionを投げるため、TextFileExceptionになる。
        Path file = tempDir.resolve("no-such-dir/memo.txt");

        assertThrows(TextFileException.class, () -> service.save(file, "内容"));
    }
}
