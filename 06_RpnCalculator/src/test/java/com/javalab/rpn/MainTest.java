package com.javalab.rpn;

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
    void invalidExpressionShowsErrorAndContinuesWithoutCrashing() {
        // "3 x +" は不正トークンを含む式。エラー表示のみで異常終了せず、
        // 直後の正常な式 "3 4 +" が処理され続けることを確認する。
        Scanner scanner = new Scanner(new StringReader("3 x +\n3 4 +\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
        assertTrue(output.contains("7"));
    }

    @Test
    void validExpressionShowsResult() {
        Scanner scanner = new Scanner(new StringReader("2 3 4 + *\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("= 14"));
    }
}
