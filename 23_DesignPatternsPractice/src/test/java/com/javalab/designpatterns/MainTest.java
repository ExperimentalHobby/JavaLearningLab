package com.javalab.designpatterns;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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
