package com.javalab.junitpractice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream)} のREPLループを結合テストするクラス。
 * {@link OrderNotificationServiceTest}とは対照的に、{@link EmailSender}はモックせず
 * 実際の{@link ConsoleEmailSender}を使い、標準出力への出力内容をそのまま検証する
 * (実オブジェクトによる統合テストの例)。
 */
@Tag("integration")
@DisplayName("Main")
class MainTest {

    @Test
    @DisplayName("orderコマンドで注文確認メールが送信される")
    void runSendsNotificationForOrderCommand() {
        Scanner scanner = new Scanner("order alice@example.com 1000\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("To: alice@example.com"));
        assertTrue(result.contains("合計金額 1000円"));
    }

    @Test
    @DisplayName("不明なコマンドはエラー表示後も処理を継続する")
    void runShowsErrorAndContinuesForUnknownCommand() {
        Scanner scanner = new Scanner("foobar\norder bob@example.com 500\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("不明なコマンドです"));
        assertTrue(result.contains("To: bob@example.com"));
    }

    @Test
    @DisplayName("合計金額0円以下の注文はスキップ理由が表示される")
    void runShowsSkipReasonForNonPositiveTotalOrder() {
        // 修正前はスキップ時に何も表示されず、利用者には「何も起きなかった」ようにしか見えなかった。
        Scanner scanner = new Scanner("order carol@example.com 0\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("合計金額が0円以下のため送信をスキップしました"));
    }

    @Test
    @DisplayName("不正な形式のメールアドレスはスキップ理由が表示される")
    void runShowsSkipReasonForInvalidEmailOrder() {
        Scanner scanner = new Scanner("order not-an-email 1000\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("メールアドレスの形式が不正なため送信をスキップしました"));
    }

    @Test
    @DisplayName("orderコマンドの引数が不足していると分かりやすいエラーが表示される")
    void runShowsClearErrorForOrderCommandWithMissingArguments() {
        // 修正前はparts[2]でArrayIndexOutOfBoundsExceptionとなり、
        // 「エラー: Index 2 out of bounds for length 2」という英語の内部例外メッセージが表示されていた。
        Scanner scanner = new Scanner("order alice@example.com\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("使用方法: order <メールアドレス> <金額>"));
    }
}
