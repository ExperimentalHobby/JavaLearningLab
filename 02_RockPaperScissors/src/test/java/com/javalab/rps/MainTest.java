package com.javalab.rps;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void invalidHandInputShowsErrorAndContinuesWithoutCrashing() {
        Scanner scanner = new Scanner(new StringReader("あいうえお\nグー\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, () -> Hand.SCISSORS);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void playerWinsWithFixedComputerHandShowsWinMessage() {
        Scanner scanner = new Scanner(new StringReader("グー\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, () -> Hand.SCISSORS);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("あなたの勝ちです"));
    }
}
