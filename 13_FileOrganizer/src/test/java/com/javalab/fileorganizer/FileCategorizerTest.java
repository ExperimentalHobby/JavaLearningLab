package com.javalab.fileorganizer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileCategorizerTest {

    @Test
    void categorizesImageExtension() {
        assertEquals("images", FileCategorizer.categoryOf(Path.of("photo.jpg")));
    }

    @Test
    void categorizesTextExtension() {
        assertEquals("text", FileCategorizer.categoryOf(Path.of("notes.txt")));
    }

    @Test
    void categorizesDocumentExtension() {
        assertEquals("documents", FileCategorizer.categoryOf(Path.of("report.pdf")));
    }

    @Test
    void categorizesUnknownExtensionAsOthers() {
        assertEquals("others", FileCategorizer.categoryOf(Path.of("data.xyz")));
    }

    @Test
    void categorizesFileWithoutExtensionAsOthers() {
        assertEquals("others", FileCategorizer.categoryOf(Path.of("noextension")));
    }
}
