package com.javalab.jacksonjsonmapping;

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
    void addThenToJson_showsFormattedPrice() {
        String output = runCommands("""
                add P001 ノート 150 2026-04-01
                toJson
                exit
                """);

        assertTrue(output.contains("\"price\":\"150円\""));
    }

    @Test
    void addThenToXml_showsProductsElement() {
        String output = runCommands("""
                add P001 ノート 150 2026-04-01
                toXml
                exit
                """);

        assertTrue(output.contains("<products>"));
        assertTrue(output.contains("<price>150円</price>"));
    }

    @Test
    void invalidPrice_showsErrorAndContinues() {
        String output = runCommands("""
                add P001 ノート abc 2026-04-01
                list
                exit
                """);

        assertTrue(output.contains("エラー: 数値または日付の形式が不正です"));
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
