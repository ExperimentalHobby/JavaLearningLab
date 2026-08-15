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
}
