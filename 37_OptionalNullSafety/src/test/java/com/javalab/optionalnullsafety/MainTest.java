package com.javalab.optionalnullsafety;

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
    void addThenFindById_showsEmployee() {
        String output = runCommands("""
                add E001 山田太郎 yamada@example.com
                find E001
                exit
                """);

        assertTrue(output.contains("E001 山田太郎 yamada@example.com"));
    }

    @Test
    void addThenFindByEmail_showsEmployee() {
        String output = runCommands("""
                add E001 山田太郎 yamada@example.com
                find yamada@example.com
                exit
                """);

        assertTrue(output.contains("E001 山田太郎 yamada@example.com"));
    }

    @Test
    void findUnknown_showsNotFoundMessageAndContinues() {
        String output = runCommands("""
                find nobody
                exit
                """);

        assertTrue(output.contains("該当する社員が見つかりません: nobody"));
    }

    @Test
    void emailForUnknownId_showsErrorAndContinues() {
        String output = runCommands("""
                email E999
                exit
                """);

        assertTrue(output.contains("エラー: employee not found: id=E999"));
    }

    @Test
    void unknownCommand_showsErrorAndContinues() {
        String output = runCommands("""
                foo bar
                exit
                """);

        assertTrue(output.contains("エラー: 不明なコマンドです: foo bar"));
    }
}
