package com.javalab.modernjavasyntax;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

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

        assertTrue(output.contains("エラー: cannot deliver from Placed"));
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
}
