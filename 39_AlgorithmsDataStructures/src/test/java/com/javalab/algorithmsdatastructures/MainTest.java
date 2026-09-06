package com.javalab.algorithmsdatastructures;

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
    void sortQuick_showsSortedValues() {
        String output = runCommands("""
                sort quick 5 3 8 3 1
                exit
                """);

        assertTrue(output.contains("1 3 3 5 8"));
    }

    @Test
    void bstInsertThenInorder_showsSortedValues() {
        String output = runCommands("""
                bst insert 5
                bst insert 3
                bst insert 8
                bst inorder
                exit
                """);

        assertTrue(output.contains("3 5 8"));
    }

    @Test
    void bstContains_showsTrueOrFalse() {
        String output = runCommands("""
                bst insert 5
                bst contains 5
                bst contains 999
                exit
                """);

        assertTrue(output.contains("true"));
        assertTrue(output.contains("false"));
    }

    @Test
    void sortWithInvalidNumber_showsErrorAndContinues() {
        String output = runCommands("""
                sort quick abc
                exit
                """);

        assertTrue(output.contains("エラー: 数値の形式が不正です"));
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
