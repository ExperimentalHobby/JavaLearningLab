package com.javalab.shapes;

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
    void invalidCommandShowsErrorAndContinuesWithoutCrashing() {
        // "foo" はcircle/rectangle/triangle/listのいずれにも一致しない不正コマンド。
        // エラー表示のみで継続し、直後の "circle 5" が正常に処理されることを確認する。
        Scanner scanner = new Scanner(new StringReader("foo\ncircle 5\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void circleRectangleListSequenceShowsTotalArea() {
        // 円(半径5、面積≒78.54)と長方形(4×5、面積20)を登録した後のlistで、
        // 異なる図形の面積が合算された合計値(≒98.54)が表示されることを確認する。
        Scanner scanner = new Scanner(new StringReader("circle 5\nrectangle 4 5\nlist\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("合計面積=98.54"));
    }

    @Test
    void triangleCommandAddsShapeReflectedInListTotalArea() {
        // 3-4-5の直角三角形(面積6.00)を登録し、listの合計面積に反映されることを確認する。
        Scanner scanner = new Scanner(new StringReader("triangle 3 4 5\nlist\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("合計面積=6.00"));
    }

    @Test
    void triangleCommandShowsErrorForInvalidTriangleAndContinues() {
        // "1 1 5"は三角不等式(1+1<=5)を満たさない不正な三角形。
        // エラー表示のみで継続し、直後の正常な "triangle 3 4 5" が処理されることを確認する。
        Scanner scanner = new Scanner(new StringReader("triangle 1 1 5\ntriangle 3 4 5\nlist\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
        assertTrue(output.contains("合計面積=6.00"));
    }
}
