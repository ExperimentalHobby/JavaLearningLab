package com.javalab.quiz;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void showsFinalScoreWhenAllAnswersAreCorrect() {
        List<Question> questions = List.of(
                new Question("Javaの生みの親は誰?", "James Gosling"),
                new Question("1+1は?", "2"));
        Scanner scanner = new Scanner(new StringReader("James Gosling\n2\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, questions);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("2/2"));
    }

    @Test
    void showsCorrectAndIncorrectFeedbackAndFinalScore() {
        List<Question> questions = List.of(
                new Question("Javaの生みの親は誰?", "James Gosling"),
                new Question("1+1は?", "2"));
        Scanner scanner = new Scanner(new StringReader("James Gosling\n99\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, questions);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("正解!"));
        assertTrue(output.contains("不正解"));
        assertTrue(output.contains("1/2"));
    }
}
