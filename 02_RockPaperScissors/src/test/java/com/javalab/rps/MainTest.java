package com.javalab.rps;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream, java.util.function.Supplier)} のREPLループを検証するテスト。
 * コンピュータの手を乱数ではなく固定値のSupplierに差し替えることで、勝敗を決定的に再現できる。
 */
class MainTest {

    @Test
    void invalidHandInputShowsErrorAndContinuesWithoutCrashing() {
        // 「あいうえお」は「グー/チョキ/パー」のいずれにも一致しない不正入力。
        // ここでエラー表示のみ行い、次の「グー」入力が正常に処理され続けることを確認する。
        Scanner scanner = new Scanner(new StringReader("あいうえお\nグー\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, () -> Hand.SCISSORS);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void playerWinsWithFixedComputerHandShowsWinMessage() {
        // コンピュータの手を常にSCISSORS(チョキ)に固定し、プレイヤーがグーを出せば
        // 必ず勝つという決定的な状況を作って、勝利メッセージが表示されることを確認する。
        Scanner scanner = new Scanner(new StringReader("グー\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, () -> Hand.SCISSORS);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("あなたの勝ちです"));
    }
}
