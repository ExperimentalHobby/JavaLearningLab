package com.javalab.unitconverter;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream)} のREPLループを結合テストするクラス。
 */
class MainTest {

    @Test
    void invalidInputShowsErrorAndContinuesWithoutCrashing() {
        // "abc km m" は数値部分が解析できない不正入力。エラー表示のみで異常終了せず、
        // 続く "5 km m" が正常に処理される(5000が出力される)ことを確認する。
        Scanner scanner = new Scanner(new StringReader("abc km m\n5 km m\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
        assertTrue(output.contains("5000"));
    }

    @Test
    void validConversionShowsResult() {
        // 正常な入力に対して "= 1000 g" のような変換先単位付きの結果行が表示されることを確認する。
        Scanner scanner = new Scanner(new StringReader("1 kg g\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("1000 g"));
    }

    @Test
    void unitInputIsCaseInsensitive() {
        // "5 KM m" のような大文字表記でも「不明な単位です」にならず変換できることを確認する。
        Scanner scanner = new Scanner(new StringReader("5 KM m\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("= 5000 m"));
    }

    @Test
    void tinyResultIsShownAsPlainDecimalNotScientificNotation() {
        // doubleのまま計算していた頃は "1 mm km" が "= 1.0E-6 km" のような指数表記になっていた。
        // BigDecimal化後は指数表記を使わない通常の小数表記になることを確認する。
        Scanner scanner = new Scanner(new StringReader("1 mm km\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("= 0.000001 km"));
    }
}
