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
