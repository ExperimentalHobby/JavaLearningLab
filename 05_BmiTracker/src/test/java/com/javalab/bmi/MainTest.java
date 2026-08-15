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

/**
 * {@link Main#run(Scanner, PrintStream, File, java.util.function.Supplier)} のREPLループを結合テストするクラス。
 * 測定日は{@code () -> LocalDate.of(2026, 8, 1)}のように固定値のSupplierへ差し替え、
 * 「今日の日付」に依存せず決定的に検証できるようにしている。
 */
class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void invalidInputShowsErrorAndContinuesWithoutCrashing() {
        // "abc 65" は身長部分が数値として解析できない不正入力。エラー表示のみで継続し、
        // 直後の正常な "add 170 65" が問題なく処理されることを確認する。
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
        // add→list→saveの一連の操作で、listがCSV形式の行を表示し、
        // saveが実際にファイルへ書き出すことを両方確認する。
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
