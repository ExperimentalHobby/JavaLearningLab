package com.javalab.fileorganizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void invalidPathShowsErrorAndContinuesWithoutCrashing() throws Exception {
        Path missing = tempDir.resolve("does-not-exist");
        Files.createFile(tempDir.resolve("photo.jpg"));
        Scanner scanner = new Scanner(new StringReader(
                "organize " + missing + "\norganize " + tempDir + "\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void organizeCommandShowsResultSummary() throws Exception {
        Files.createFile(tempDir.resolve("photo.jpg"));
        Scanner scanner = new Scanner(new StringReader("organize " + tempDir + "\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("1件のファイルを整理しました"));
    }
}
