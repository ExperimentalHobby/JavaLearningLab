package com.javalab.rpstournament;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream, java.util.function.Function)} のREPLループを結合テストするクラス。
 */
class MainTest {

    @Test
    void invalidCommandShowsErrorAndContinuesWithoutCrashing() {
        // "foo" はregister/startのいずれにも一致しない不正コマンド。
        // エラー表示のみで継続し、直後の "register Alice Bob" が正常に処理されることを確認する。
        Scanner scanner = new Scanner(new StringReader("foo\nregister Alice Bob\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, player -> Hand.ROCK);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("エラー"));
    }

    @Test
    void registerAndStartShowsChampion() {
        // Aliceは常にROCK、Bob以外(=Bob)は常にSCISSORSを出すよう手を固定し、
        // Aliceが確実に勝って優勝者として表示されることを確認する。
        Scanner scanner = new Scanner(new StringReader("register Alice Bob\nstart\nexit\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, player -> player.getName().equals("Alice") ? Hand.ROCK : Hand.SCISSORS);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("優勝: Alice"));
    }
}
