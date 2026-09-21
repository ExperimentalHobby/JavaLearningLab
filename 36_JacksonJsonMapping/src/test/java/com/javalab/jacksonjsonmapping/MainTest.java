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

    @Test
    void fromJsonMalformedInput_showsErrorAndContinues() {
        // 修正前はREPLごと落ちていた。
        String output = runCommands("""
                fromJson {bad
                list
                exit
                """);

        assertTrue(output.contains("エラー: 不正なJSON形式です"));
    }

    @Test
    void fromXmlThenList_addsProduct() {
        // fromXmlを呼び出す手段がなかった問題への対応。
        String xml = ProductXmlMapper.toXml(
                new Product("P001", "ノート", new java.math.BigDecimal("150"), java.time.LocalDate.of(2026, 4, 1)));

        String output = runCommands("""
                fromXml %s
                list
                exit
                """.formatted(xml));

        assertTrue(output.contains("ID: P001"));
    }

    @Test
    void list_showsFormattedFieldsInsteadOfRawToString() {
        String output = runCommands("""
                add P001 ノート 150 2026-04-01
                list
                exit
                """);

        assertTrue(output.contains("ID: P001, 商品名: ノート, 価格: 150円, 発売日: 2026-04-01"));
    }
}
