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

        assertTrue(output.contains("エラー: 該当する社員が見つかりません: id=E999"));
    }

    @Test
    void domainForExistingId_showsEmailDomain() {
        String output = runCommands("""
                add E001 山田太郎 yamada@example.com
                domain E001
                exit
                """);

        assertTrue(output.contains("example.com"));
    }

    @Test
    void domainForUnknownId_showsNotFoundMessage() {
        String output = runCommands("""
                domain E999
                exit
                """);

        assertTrue(output.contains("該当する社員が見つかりません: E999"));
    }

    @Test
    void existsForExistingId_showsFoundMessage() {
        // ifPresent(elseなし)の活用例。
        String output = runCommands("""
                add E001 山田太郎 yamada@example.com
                exists E001
                exit
                """);

        assertTrue(output.contains("見つかりました: 山田太郎"));
    }

    @Test
    void existsForUnknownId_printsNothing() {
        String output = runCommands("""
                exists E999
                exit
                """);

        assertTrue(output.lines().noneMatch(line -> line.contains("見つかりました")));
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
