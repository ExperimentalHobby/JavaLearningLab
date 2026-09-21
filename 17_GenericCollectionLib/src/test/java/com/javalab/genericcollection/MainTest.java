package com.javalab.genericcollection;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream)} のREPLループを結合テストするクラス。
 */
class MainTest {

    @Test
    void runHandlesStackAndQueueCommands() {
        Scanner scanner = new Scanner(
                "stack push A\n"
                        + "stack peek\n"
                        + "stack pop\n"
                        + "queue enqueue X\n"
                        + "queue peek\n"
                        + "queue dequeue\n"
                        + "exit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("push しました: A"));
        assertTrue(result.contains("peek: A"));
        assertTrue(result.contains("pop しました: A"));
        assertTrue(result.contains("enqueue しました: X"));
        assertTrue(result.contains("peek: X"));
        assertTrue(result.contains("dequeue しました: X"));
    }

    @Test
    void runShowsErrorAndContinuesForEmptyStackPop() {
        Scanner scanner = new Scanner("stack pop\nstack push A\nstack pop\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("エラー"));
        assertTrue(result.contains("pop しました: A"));
    }

    @Test
    void runHandlesListMaxMinFilterMapCommands() {
        // CollectionUtils.maxは実装もテストもあるのにCLIから呼び出す手段がなく、
        // min/filter/mapなど汎用操作も未提供だった問題への対応。
        Scanner scanner = new Scanner(
                "list add banana\n"
                        + "list add apple\n"
                        + "list add cherry\n"
                        + "list max\n"
                        + "list min\n"
                        + "list filter an\n"
                        + "list map upper\n"
                        + "exit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("max: cherry"));
        assertTrue(result.contains("min: apple"));
        assertTrue(result.contains("filter: [banana]"));
        assertTrue(result.contains("map: [BANANA, APPLE, CHERRY]"));
    }

    @Test
    void runShowsErrorAndContinuesForStackPushWithoutValue() {
        // "stack push"(値省略)はhandleStackがparts[2]へ無条件にアクセスし
        // ArrayIndexOutOfBoundsExceptionでREPLごと落ちていた問題への対応。
        Scanner scanner = new Scanner("stack push\nstack push A\nstack pop\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("使い方") || result.contains("エラー"));
        assertTrue(result.contains("pop しました: A"));
    }

    @Test
    void runShowsErrorAndContinuesForQueueEnqueueWithoutValue() {
        Scanner scanner = new Scanner("queue enqueue\nqueue enqueue X\nqueue dequeue\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("使い方") || result.contains("エラー"));
        assertTrue(result.contains("dequeue しました: X"));
    }

    @Test
    void runShowsErrorAndContinuesForUnknownCommand() {
        Scanner scanner = new Scanner("foobar\nstack push A\nstack pop\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("不明なコマンドです"));
        assertTrue(result.contains("pop しました: A"));
    }
}
