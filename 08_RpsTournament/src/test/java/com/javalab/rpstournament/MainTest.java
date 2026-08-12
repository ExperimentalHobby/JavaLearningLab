package com.javalab.rpstournament;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void invalidCommandShowsErrorAndContinuesWithoutCrashing() {
        Scanner scanner = new Scanner(new StringReader("foo\nregister Alice Bob\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, player -> Hand.ROCK);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void registerAndStartShowsChampion() {
        Scanner scanner = new Scanner(new StringReader("register Alice Bob\nstart\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, player -> player.getName().equals("Alice") ? Hand.ROCK : Hand.SCISSORS);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("優勝: Alice"));
    }
}
