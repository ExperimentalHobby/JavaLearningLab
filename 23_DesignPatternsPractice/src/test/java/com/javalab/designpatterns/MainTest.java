package com.javalab.designpatterns;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream)} のREPLループを結合テストするクラス。
 * Singleton/Factory/Observer/Strategyの4パターンそれぞれに対応するコマンドを一通り実行し、
 * REPL経由でも各パターンの実装が正しく動作することを確認する。
 */
class MainTest {

    @Test
    void runHandlesAllFourPatternCommands() {
        Scanner scanner = new Scanner(
                "log テストログ\n"
                        + "shape circle 2\n"
                        + "weather 30.0\n"
                        + "pay creditcard 1000\n"
                        + "exit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("ログに記録しました: テストログ"));
        assertTrue(result.contains("面積: " + (Math.PI * 4.0)));
        assertTrue(result.contains("気温が変化しました: 30.0℃"));
        assertTrue(result.contains("クレジットカードで1000円を決済しました"));
    }

    @Test
    void runLogsCommandPrintsRecordedLogMessages() {
        // logコマンドで貯め込むだけでlogs()を呼ぶ経路がなく、Singletonの眼目である
        // 「どこから呼んでも同じインスタンス・同じ状態を参照する」ことを確認する手段が
        // なかった問題への対応。AppLoggerはJVM全体で共有される真のSingletonなので、
        // 「記録する実行」と「表示する実行」を別のMain.run呼び出しに分けることで、
        // logsコマンドの出力そのものにメッセージが含まれることを検証する
        // (同一実行内だとlogコマンド自身のechoで偽陽性になってしまうため)。
        String uniqueMessage = "一意なログメッセージ-" + System.nanoTime();
        runCommands("log " + uniqueMessage + "\nexit\n");

        String result = runCommands("logs\nexit\n");

        assertTrue(result.contains(uniqueMessage));
    }

    private String runCommands(String input) {
        Scanner scanner = new Scanner(input);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void runPayCommandWithDiscountPercentAppliesDecoratorBeforeDelegating() {
        // Decoratorパターン学習用に追加した機能: payコマンドに割引率を渡すと、
        // 既存のPaymentStrategyをDiscountedPaymentでラップしてから決済する。
        String result = runCommands("pay creditcard 1000 10\nexit\n");

        assertTrue(result.contains("クレジットカードで900円を決済しました(10%割引適用)"));
    }

    @Test
    void runWeatherUnsubscribeCommandStopsConsoleObserverFromReceivingNotifications() {
        // WeatherStationのunsubscribeをCLIから呼ぶ手段がなかった問題への対応。
        // unsubscribe前は通知メッセージが出るが、unsubscribe後は出なくなることを確認する。
        String result = runCommands("weather 30.0\nweather unsubscribe\nweather 40.0\nexit\n");

        assertTrue(result.contains("気温が変化しました: 30.0℃"));
        assertFalse(result.contains("気温が変化しました: 40.0℃"));
    }

    @Test
    void runShowsErrorAndContinuesForUnknownCommand() {
        Scanner scanner = new Scanner("foobar\nlog 継続確認\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("不明なコマンドです"));
        assertTrue(result.contains("ログに記録しました: 継続確認"));
    }
}
