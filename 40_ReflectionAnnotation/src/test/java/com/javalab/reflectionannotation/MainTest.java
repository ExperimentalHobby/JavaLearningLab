package com.javalab.reflectionannotation;

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
    void registerValidUser_showsSuccessMessage() {
        String output = runCommands("""
                register 山田太郎 30 yamada@example.com
                exit
                """);

        assertTrue(output.contains("登録成功: 山田太郎"));
    }

    @Test
    void registerInvalidAge_showsViolationMessage() {
        String output = runCommands("""
                register 山田太郎 200 yamada@example.com
                exit
                """);

        assertTrue(output.contains("違反: age must be <= 150"));
    }

    @Test
    void registerNonNumericAge_showsFormatError() {
        String output = runCommands("""
                register 山田太郎 abc yamada@example.com
                exit
                """);

        assertTrue(output.contains("エラー: 年齢の形式が不正です"));
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
