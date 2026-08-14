package com.javalab.junitpractice;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
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
    void runShowsErrorAndContinuesForUnknownCommand() {
        Scanner scanner = new Scanner("foobar\norder bob@example.com 500\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("不明なコマンドです"));
        assertTrue(result.contains("To: bob@example.com"));
    }
}
