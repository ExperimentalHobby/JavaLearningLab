package com.javalab.loggingtool;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream, BatchJobRunner, LogLevelController)} の
 * REPLループを結合テストするクラス。
 */
class MainTest {

    @Test
    void runExecutesBatchAndShowsSummary() {
        Scanner scanner = new Scanner("run job1,job2\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, new BatchJobRunner(), new LogLevelController());

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("成功=2"));
    }

    @Test
    void runChangesLogLevelViaLevelCommand() {
        Scanner scanner = new Scanner("level com.javalab.loggingtool.maintest ERROR\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        LogLevelController levelController = new LogLevelController();

        Main.run(scanner, out, new BatchJobRunner(), levelController);

        assertEquals(Level.ERROR, levelController.getLevel("com.javalab.loggingtool.maintest"));
        assertTrue(buffer.toString(StandardCharsets.UTF_8).contains("ログレベルを変更しました"));
    }

    @Test
    void runShowsErrorAndContinuesForUnknownCommand() {
        Scanner scanner = new Scanner("foobar\nrun job1\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, new BatchJobRunner(), new LogLevelController());

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("不明なコマンドです"));
        assertTrue(result.contains("成功=1"));
    }

    @Test
    void runShowsClearErrorForLevelCommandWithMissingLevelArgument() {
        // 修正前はargs[1]でArrayIndexOutOfBoundsExceptionとなり、
        // 「エラー: Index 1 out of bounds for length 1」という英語の内部例外メッセージが表示されていた。
        Scanner scanner = new Scanner("level com.javalab.loggingtool.maintest\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, new BatchJobRunner(), new LogLevelController());

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("使用方法: level <ロガー名> <LEVEL>"));
    }

    @Test
    void runSummaryCommandShowsLevelCounts(@TempDir Path tempDir) throws IOException {
        // フォルダ名・READMEの「ログ収集&解析ツール」に見合う機能として追加したsummaryコマンド。
        // このツール自身が出力したログファイルを読み込みレベル別件数を表示する。
        Path logFile = tempDir.resolve("app.log");
        Files.write(logFile, List.of(
                "2026-09-21 10:00:00.000 [main] INFO  c.javalab.loggingtool.BatchJobRunner [abc123] - 開始",
                "2026-09-21 10:00:00.001 [main] INFO  c.javalab.loggingtool.BatchJobRunner [abc123] - 完了",
                "2026-09-21 10:00:00.002 [main] ERROR c.javalab.loggingtool.BatchJobRunner [abc123] - 失敗"),
                StandardCharsets.UTF_8);
        Scanner scanner = new Scanner("summary " + logFile + "\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, new BatchJobRunner(), new LogLevelController());

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("INFO: 2"));
        assertTrue(result.contains("ERROR: 1"));
    }

    @Test
    void runShowsClearErrorForLevelCommandWithUnknownLevelName() {
        // Level.valueOf()は不明な文字列に対して例外を投げずDEBUGを返す仕様のため、
        // 修正前は「ログレベルを変更しました: ... -> DEBUG」と表示され、
        // 指定したレベルが通ったと誤解する問題があった。
        Scanner scanner = new Scanner("level com.javalab.loggingtool.maintest BOGUS\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, new BatchJobRunner(), new LogLevelController());

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("不正なログレベルです: BOGUS"));
    }

    @Test
    void runShowsClearErrorForRunCommandWithMissingJobNamesArgument() {
        // 修正前はparts[1]でArrayIndexOutOfBoundsExceptionとなり、英語の内部例外メッセージが
        // 表示されていた。
        Scanner scanner = new Scanner("run\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, new BatchJobRunner(), new LogLevelController());

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("使用方法: run <job1,job2,...>"));
    }
}
