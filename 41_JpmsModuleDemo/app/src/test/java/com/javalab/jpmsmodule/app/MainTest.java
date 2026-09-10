package com.javalab.jpmsmodule.app;

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
    void greet_showsGreetingMessage() {
        String output = runCommands("""
                greet 太郎
                exit
                """);

        assertTrue(output.contains("こんにちは、太郎さん!"));
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
