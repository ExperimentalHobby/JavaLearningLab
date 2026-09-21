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
    void acceptsHiraganaAndEnglishHandInput() {
        // 全角カタカナ(グー/チョキ/パー)のみでなく、ひらがな・英語(大小問わず)・頭文字も
        // 受け付けることを確認する。
        Scanner scanner = new Scanner(new StringReader("ぐー\nRock\np\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, () -> Hand.SCISSORS);

        String output = outContent.toString(StandardCharsets.UTF_8);
        // 3回とも「グー」「グー」「パー」として認識され、エラーが出ないことを確認する。
        assertTrue(output.contains("あなた: グー / コンピュータ: チョキ"));
        assertTrue(output.contains("あなた: パー / コンピュータ: チョキ"));
        org.junit.jupiter.api.Assertions.assertFalse(output.contains("エラー"));
    }

    @Test
    void showsCumulativeScoreAcrossMultipleRounds() {
        // 1回ごとの結果しか表示されない問題を解消するため、通算成績(勝敗数・引き分け数)が
        // ラウンドを重ねるごとに正しく積算・表示されることを確認する。
        // コンピュータの手を常にチョキに固定: グー(勝ち)→チョキ(引き分け)→パー(負け)。
        Scanner scanner = new Scanner(new StringReader("グー\nチョキ\nパー\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, () -> Hand.SCISSORS);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("現在の成績: 1勝1敗1分け"));
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
