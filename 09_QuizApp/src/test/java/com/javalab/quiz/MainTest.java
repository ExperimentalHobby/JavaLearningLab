package com.javalab.quiz;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream, List)} の出題ループを結合テストするクラス。
 */
class MainTest {

    @Test
    void showsFinalScoreWhenAllAnswersAreCorrect() {
        // 2問とも正解を入力し、最終結果が "2/2" と表示されることを確認する。
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
        // 1問目は正解、2問目は不正解("99")を入力し、各問ごとの正誤フィードバックと
        // 最終結果("1/2")の両方が表示されることを確認する。
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
