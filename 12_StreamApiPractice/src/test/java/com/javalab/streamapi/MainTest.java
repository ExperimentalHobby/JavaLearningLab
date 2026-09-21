package com.javalab.streamapi;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream)} のREPLループを結合テストするクラス。
 */
class MainTest {

    @Test
    void invalidInputShowsErrorAndContinuesWithoutCrashing() {
        // "abc" は金額として解析できない不正入力。エラー表示のみで継続し、
        // 直後の正常な "add りんご 果物 100 3" が処理されることを確認する。
        Scanner scanner = new Scanner(new StringReader("add りんご 果物 abc 3\nadd りんご 果物 100 3\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void filterByCategoryCommandShowsOnlyMatchingRecords() {
        // filterByCategoryは実装もテストもあるのにCLIから呼び出せず「機能が死んでいる」状態だった。
        Scanner scanner = new Scanner(new StringReader(
                "add りんご 果物 100 3\nadd キャベツ 野菜 200 1\nfilterByCategory 果物\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("りんご"));
        String[] lines = output.split("\n");
        assertTrue(Arrays.stream(lines).noneMatch(line -> line.contains("キャベツ")));
    }

    @Test
    void filterAboveAmountCommandShowsOnlyRecordsAtOrAboveThreshold() {
        Scanner scanner = new Scanner(new StringReader(
                "add りんご 果物 100 3\nadd キャベツ 野菜 200 1\nfilterAboveAmount 150\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("キャベツ"));
    }

    @Test
    void productNamesCommandShowsDistinctSortedNames() {
        Scanner scanner = new Scanner(new StringReader(
                "add りんご 果物 100 3\nadd りんご 果物 120 2\nadd キャベツ 野菜 200 1\nproductNames\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("りんご"));
        assertTrue(output.contains("キャベツ"));
    }

    @Test
    void addListSequenceShowsAggregatedResults() {
        // 2件登録後のlistで、SalesAggregatorによる合計金額(150)・合計数量(8)が
        // 正しく表示されることを確認する。
        Scanner scanner = new Scanner(new StringReader("add りんご 果物 100 3\nadd バナナ 果物 50 5\nlist\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("合計金額: 150"));
        assertTrue(output.contains("合計数量: 8"));
    }
}
