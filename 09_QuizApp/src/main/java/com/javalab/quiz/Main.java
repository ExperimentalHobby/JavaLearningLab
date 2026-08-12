package com.javalab.quiz;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * 対話式CLIクイズアプリのエントリーポイント。
 * クラスパス上の{@code questions.properties}から問題を読み込み、標準入力から回答を1問ずつ受け取って
 * 正誤判定・スコア集計を行う。
 */
public class Main {

    public static void main(String[] args) throws IOException {
        List<Question> questions = QuizLoader.loadFromClasspath("/questions.properties");
        run(new Scanner(System.in), System.out, questions);
    }

    /**
     * 出題ループ本体。{@link Scanner}/{@link PrintStream} を引数として受け取ることで、
     * テストから {@code StringReader}/{@code ByteArrayOutputStream} を注入できるようにしている。
     * @param scanner 入力読み取り元
     * @param out 出力先
     * @param questions 出題する問題一覧
     */
    static void run(Scanner scanner, PrintStream out, List<Question> questions) {
        ScoreManager scoreManager = new ScoreManager();

        for (Question question : questions) {
            out.println(question.getText());
            // 入力が尽きた場合(テストでの想定外の行数など)も空文字列扱いで不正解として継続する。
            String userAnswer = scanner.hasNextLine() ? scanner.nextLine() : "";
            boolean correct = question.isCorrect(userAnswer);
            scoreManager.recordAnswer(correct);
            out.println(correct ? "正解!" : "不正解");
        }

        out.printf(
                "結果: %d/%d (%.1f%%)%n",
                scoreManager.getScore(), scoreManager.getTotal(), scoreManager.getPercentage());
    }
}
