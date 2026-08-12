package com.javalab.texteditor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TextFileServiceTest {

    private final TextFileService service = new TextFileService();

    @TempDir
    Path tempDir;

    @Test
    void saveThenLoadReturnsSameContent() {
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
        Path file = tempDir.resolve("no-such-dir/memo.txt");

        assertThrows(TextFileException.class, () -> service.save(file, "内容"));
    }
}
