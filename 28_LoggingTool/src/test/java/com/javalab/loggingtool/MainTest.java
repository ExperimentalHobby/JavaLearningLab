package com.javalab.loggingtool;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
