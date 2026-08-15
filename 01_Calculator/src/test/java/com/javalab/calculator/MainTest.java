package com.javalab.calculator;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream)} のREPLループを結合テストするクラス。
 * 標準入出力を直接使わず{@link StringReader}/{@link ByteArrayOutputStream}に差し替えることで、
 * 実際に複数行の入力を流し込んだときの一連の挙動(エラー処理・状態の継続)を検証できる。
 */
class MainTest {

    @Test
    void invalidNumberInputShowsErrorAndContinuesWithoutCrashing() {
        // "abc - 3" は数値として解析できない不正入力。ここで例外が伝播してREPLが
        // 落ちてしまわないこと、かつ直後の "2 + 3" が正常に処理され続けることを確認する。
        Scanner scanner = new Scanner(new StringReader("abc - 3\n2 + 3\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
        assertTrue(output.contains("5"));
    }

    @Test
    void memoryCommandsAccumulateAcrossMultipleCalculations() {
        // 「2+3を計算してM+」→「10-4を計算してM+」→「MRで参照」という一連の操作で、
        // メモリが単発の値ではなく複数回の計算結果を積算した値(5+6=11)になることを確認する。
        Scanner scanner = new Scanner(new StringReader("2 + 3\nM+\n10 - 4\nM+\nMR\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("メモリ: 11"));
    }
}
