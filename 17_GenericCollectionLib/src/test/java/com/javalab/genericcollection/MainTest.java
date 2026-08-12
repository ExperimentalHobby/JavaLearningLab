package com.javalab.genericcollection;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
