package com.javalab.streamapi;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void invalidInputShowsErrorAndContinuesWithoutCrashing() {
        Scanner scanner = new Scanner(new StringReader("add りんご 果物 abc 3\nadd りんご 果物 100 3\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void addListSequenceShowsAggregatedResults() {
        Scanner scanner = new Scanner(new StringReader("add りんご 果物 100 3\nadd バナナ 果物 50 5\nlist\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("合計金額: 150"));
        assertTrue(output.contains("合計数量: 8"));
    }
}
