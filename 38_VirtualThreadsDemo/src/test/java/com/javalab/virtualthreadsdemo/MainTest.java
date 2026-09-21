package com.javalab.virtualthreadsdemo;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    private static String runCommands(String input) {
        Scanner scanner = new Scanner(new StringReader(input));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void check_showsStatusCodeForEachUrl() throws Exception {
        try (SlowHttpServerSupport server = SlowHttpServerSupport.start(2, 10)) {
            String urls = String.join(" ", server.urls());

            String output = runCommands("check " + urls + "\nexit\n");

            for (String url : server.urls()) {
                assertTrue(output.contains(url + " -> 200"));
            }
        }
    }

    @Test
    void checkPlatform_showsStatusCodeForEachUrl() throws Exception {
        try (SlowHttpServerSupport server = SlowHttpServerSupport.start(2, 10)) {
            String urls = String.join(" ", server.urls());

            String output = runCommands("checkPlatform 2 " + urls + "\nexit\n");

            for (String url : server.urls()) {
                assertTrue(output.contains(url + " -> 200"));
            }
        }
    }

    @Test
    void check_showsElapsedTime() throws Exception {
        try (SlowHttpServerSupport server = SlowHttpServerSupport.start(1, 10)) {
            String output = runCommands("check " + server.urls().get(0) + "\nexit\n");

            assertTrue(output.contains("所要時間: "));
            assertTrue(output.contains("ms"));
        }
    }

    @Test
    void check_unreachableUrlMixedWithValid_showsErrorForFailedUrlOnly() throws Exception {
        // 修正前はREPLごと落ちていた。
        try (SlowHttpServerSupport server = SlowHttpServerSupport.start(1, 10)) {
            String validUrl = server.urls().get(0);
            String output = runCommands("check " + validUrl + " http://localhost:1/unreachable\nexit\n");

            assertTrue(output.contains(validUrl + " -> 200"));
            assertTrue(output.contains("http://localhost:1/unreachable -> エラー: "));
        }
    }

    @Test
    void unknownCommand_showsErrorAndContinues() {
        String output = runCommands("""
                foo bar
                exit
                """);

        assertTrue(output.contains("エラー: 不明なコマンドです: foo bar"));
    }

    @Test
    void checkPlatformWithoutPoolSize_showsFormatError() {
        String output = runCommands("""
                checkPlatform http://localhost/x
                exit
                """);

        assertTrue(output.contains("エラー: 入力形式が不正です"));
    }
}
