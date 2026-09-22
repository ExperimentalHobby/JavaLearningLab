package com.javalab.builderorder;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream)} のREPLループを結合テストするクラス。
 * start→item→buildという段階的な注文構築の流れと、途中でのエラー処理を確認する。
 */
class MainTest {

    @Test
    void runBuildsOrderAndPrintsSummaryThroughStartItemBuildFlow() {
        Scanner scanner = new Scanner("start 山田太郎 東京都渋谷区1-1-1\nitem ノート 3 150\nbuild\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("山田太郎"));
        assertTrue(result.contains("450"));
    }

    @Test
    void runShowsErrorWhenItemIsUsedBeforeStart() {
        Scanner scanner = new Scanner("item ノート 3 150\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("先にstartで注文を開始してください"));
    }

    @Test
    void runShowsErrorForBuildWithNoItemsAndContinuesAcceptingCommands() {
        Scanner scanner = new Scanner("start 山田太郎 東京都渋谷区1-1-1\nbuild\nitem ノート 3 150\nbuild\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("商品が1件も追加されていません"));
        assertTrue(result.contains("450"));
    }

    @Test
    void runShowsErrorForWrapCommandWithInvalidArgument() {
        // wrap xyzはparts[1].equals("on")がfalseなのでoffとして設定されるのに、
        // 「ギフトラッピングを設定しました: xyz」と表示され実際の設定と表示が食い違っていた問題への対応。
        Scanner scanner = new Scanner("start 山田太郎 東京都渋谷区1-1-1\nwrap xyz\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("使用方法: wrap on|off"));
    }

    @Test
    void runShowsJapaneseUsageMessageAndContinuesWhenStartIsMissingArguments() {
        // 修正前はparts[1]/parts[2]への無検証な添字アクセスでArrayIndexOutOfBoundsExceptionが発生し、
        // catch (RuntimeException)経由で英語の内部例外メッセージがそのまま表示されていた。
        Scanner scanner = new Scanner("start 山田太郎\nstart 山田太郎 東京都渋谷区1-1-1\nbuild\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("エラー: 使用方法: start <顧客名> <配送先住所>"));
        assertTrue(result.contains("山田太郎"));
    }

    @Test
    void runShowsJapaneseUsageMessageAndContinuesWhenItemIsMissingArguments() {
        Scanner scanner = new Scanner("start 山田太郎 東京都渋谷区1-1-1\nitem ノート 3\nitem ノート 3 150\nbuild\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("エラー: 使用方法: item <商品名> <数量> <単価>"));
        assertTrue(result.contains("450"));
    }

    @Test
    void runShowsJapaneseUsageMessageAndContinuesWhenPaymentIsMissingArguments() {
        Scanner scanner = new Scanner("start 山田太郎 東京都渋谷区1-1-1\npayment\npayment credit\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("エラー: 使用方法: payment <方法>"));
        assertTrue(result.contains("支払方法を設定しました: credit"));
    }

    @Test
    void runShowsJapaneseUsageMessageAndContinuesWhenWrapIsMissingArguments() {
        Scanner scanner = new Scanner("start 山田太郎 東京都渋谷区1-1-1\nwrap\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("エラー: 使用方法: wrap on|off"));
    }
}
