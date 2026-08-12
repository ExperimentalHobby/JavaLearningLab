package com.javalab.loganalyzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void runShowsParseResultForLoadCommand() throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.writeString(logFile, String.join("\n",
                "2026-08-13 10:15:30 [ERROR] データベース接続に失敗しました",
                "これはログ行ではありません",
                "2026-08-13 10:16:00 [INFO] 処理を開始しました"), StandardCharsets.UTF_8);

        Scanner scanner = new Scanner("load " + logFile + "\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("解析成功: 2件"));
        assertTrue(result.contains("スキップ: 1件"));
        assertTrue(result.contains("ERROR: 1件"));
        assertTrue(result.contains("INFO: 1件"));
    }

    @Test
    void runShowsErrorAndContinuesForUnknownCommand() {
        Scanner scanner = new Scanner("foobar\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("不明なコマンドです"));
    }

    @Test
    void runShowsErrorAndContinuesForNonExistentFile() {
        Scanner scanner = new Scanner("load " + tempDir.resolve("no-such-file.log") + "\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("エラー"));
    }
}
