package com.javalab.todo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void invalidCommandShowsErrorAndContinuesWithoutCrashing() {
        Scanner scanner = new Scanner(new StringReader("foo\nadd 牛乳を買う\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);
        File file = tempDir.resolve("todo.txt").toFile();

        Main.run(scanner, out, file);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void addListDoneSequenceWorksCorrectly() {
        Scanner scanner = new Scanner(new StringReader("add 牛乳を買う\nlist\ndone 0\nlist\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);
        File file = tempDir.resolve("todo.txt").toFile();

        Main.run(scanner, out, file);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("0: [ ] 牛乳を買う"));
        assertTrue(output.contains("0: [x] 牛乳を買う"));
    }
}
