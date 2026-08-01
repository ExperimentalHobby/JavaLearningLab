package com.javalab.bmi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void invalidInputShowsErrorAndContinuesWithoutCrashing() {
        Scanner scanner = new Scanner(new StringReader("abc 65\nadd 170 65\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);
        File file = tempDir.resolve("bmi.csv").toFile();

        Main.run(scanner, out, file, () -> LocalDate.of(2026, 8, 1));

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void addListSaveSequenceWorksCorrectly() {
        File file = tempDir.resolve("bmi.csv").toFile();
        Scanner scanner = new Scanner(new StringReader("add 170 65\nlist\nsave\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, file, () -> LocalDate.of(2026, 8, 1));

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("2026-08-01,170.0,65.0,22.49,普通体重"));
        assertTrue(output.contains("保存しました"));
        assertTrue(file.exists());
    }
}
