package com.javalab.quiz;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream, List)} の出題ループを結合テストするクラス。
 */
class MainTest {

    @TempDir
    Path tempDir;

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

    @Test
    void showsCorrectAnswerWhenUserAnswerIsWrong() {
        // 「不正解」とだけ出て正解が表示されないと学習アプリとしての効果が薄いという指摘への対応。
        // 不正解時に正解("2")が併記されることを確認する。
        List<Question> questions = List.of(new Question("1+1は?", "2"));
        Scanner scanner = new Scanner(new StringReader("99\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, questions);

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("不正解(正解: 2)"));
    }

    @Test
    void loadQuestionsUsesFileFromCommandLineArgumentWhenProvided() throws IOException {
        // 「問題ファイルのコマンドライン引数指定」という学習テーマへの対応。
        // args[0]が指定されている場合、クラスパスのデフォルトではなくそのファイルから読み込む。
        File file = tempDir.resolve("custom.properties").toFile();
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.println("question.count=1");
            writer.println("q1.question=カスタム問題");
            writer.println("q1.answer=カスタム");
        }

        List<Question> questions = Main.loadQuestions(new String[] {file.getPath()});

        assertEquals(1, questions.size());
        assertEquals("カスタム問題", questions.get(0).getText());
    }

    @Test
    void loadQuestionsUsesClasspathDefaultWhenNoArgumentProvided() throws IOException {
        List<Question> questions = Main.loadQuestions(new String[0]);

        assertTrue(questions.size() > 0);
    }

    @Test
    void runWithRandomShufflesQuestionOrderWithoutLosingAnyQuestion() {
        // 「出題順のシャッフル」という学習テーマへの対応。シャッフル後も出題される問題の集合は
        // 変わらないことを、固定シードのRandomで決定的に確認する。
        List<Question> questions = new ArrayList<>(List.of(
                new Question("Q1", "A1"),
                new Question("Q2", "A2"),
                new Question("Q3", "A3")));
        Scanner scanner = new Scanner(new StringReader("A1\nA2\nA3\n"));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, questions, new Random(42));

        String output = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Q1"));
        assertTrue(output.contains("Q2"));
        assertTrue(output.contains("Q3"));
    }
}
