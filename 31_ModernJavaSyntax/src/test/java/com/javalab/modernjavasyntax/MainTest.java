package com.javalab.modernjavasyntax;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @TempDir
    Path tempDir;

    private static String runCommands(String input) {
        Scanner scanner = new Scanner(new StringReader(input));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, () -> LocalDate.of(2026, 1, 1));

        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void placeShipDeliverStatus_showsDeliveredReceipt() {
        String output = runCommands("""
                place ORD-001
                ship ORD-001 TRACK-001
                deliver ORD-001
                status ORD-001
                exit
                """);

        assertTrue(output.contains("状態: 配達完了"));
        assertTrue(output.contains("伝票番号: TRACK-001"));
    }

    @Test
    void deliverWithoutShipping_showsErrorAndContinues() {
        String output = runCommands("""
                place ORD-001
                deliver ORD-001
                status ORD-001
                exit
                """);

        assertTrue(output.contains("エラー: 注文受付の注文は配達完了にできません"));
        assertTrue(output.contains("状態: 注文受付"));
    }

    @Test
    void unknownCommand_showsErrorAndContinues() {
        String output = runCommands("""
                foo bar
                list
                exit
                """);

        assertTrue(output.contains("エラー: 不明なコマンドです: foo bar"));
    }

    @Test
    void statusForUnknownOrder_showsError() {
        String output = runCommands("""
                status ORD-999
                exit
                """);

        assertTrue(output.contains("エラー: 該当する注文がありません: ORD-999"));
    }

    @Test
    void list_showsOrderIdWithState() {
        String output = runCommands("""
                place ORD-001
                place ORD-002
                ship ORD-002 TRACK-001
                list
                exit
                """);

        assertTrue(output.contains("ORD-001: 注文受付"));
        assertTrue(output.contains("ORD-002: 発送済み"));
    }

    @Test
    void saveThenLoad_restoresOrdersFromFile() {
        Path file = tempDir.resolve("orders.json");
        String saveOutput = runCommands("""
                place ORD-001
                ship ORD-001 TRACK-001
                save %s
                exit
                """.formatted(file));
        assertTrue(saveOutput.contains("保存しました: " + file));

        String loadOutput = runCommands("""
                load %s
                status ORD-001
                exit
                """.formatted(file));

        assertTrue(loadOutput.contains("読み込みました: " + file));
        assertTrue(loadOutput.contains("状態: 発送済み"));
        assertTrue(loadOutput.contains("伝票番号: TRACK-001"));
    }

    @Test
    void loadMissingFile_showsErrorAndContinues() {
        Path missing = tempDir.resolve("missing.json");
        String output = runCommands("""
                load %s
                exit
                """.formatted(missing));

        assertTrue(output.contains("エラー: ファイルが見つかりません: " + missing));
    }

    @Test
    void placeWithExistingOrderId_showsErrorAndDoesNotOverwrite() {
        String output = runCommands("""
                place ORD-001
                ship ORD-001 TRACK-001
                deliver ORD-001
                place ORD-001
                status ORD-001
                exit
                """);

        assertTrue(output.contains("エラー: 既に存在する注文IDです: ORD-001"));
        assertTrue(output.contains("状態: 配達完了"));
    }
}
