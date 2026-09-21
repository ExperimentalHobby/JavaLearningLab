package com.javalab.quiz;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * 対話式CLIクイズアプリのエントリーポイント。
 * 問題ファイルが指定されていればそこから、未指定ならクラスパス上の{@code questions.properties}から
 * 問題を読み込み、標準入力から回答を1問ずつ受け取って正誤判定・スコア集計を行う。
 */
public class Main {

    public static void main(String[] args) {
        try {
            List<Question> questions = loadQuestions(args);
            run(new Scanner(System.in), System.out, questions, new Random());
        } catch (IOException e) {
            // 問題ファイルが読めない場合にスタックトレースをそのまま表示せず、
            // 他のアプリと同じくエラーメッセージを出して終了する。
            System.out.println("エラー: 問題ファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }

    /**
     * コマンドライン引数に応じて問題一覧を読み込む。
     * @param args コマンドライン引数。{@code args[0]}が指定されていればそのファイルパスから、
     *             未指定ならクラスパス上のデフォルト問題ファイルから読み込む。
     * @return 問題一覧
     * @throws IOException ファイル・リソースの読み込みに失敗した場合
     */
    static List<Question> loadQuestions(String[] args) throws IOException {
        if (args.length > 0) {
            return QuizLoader.loadFromProperties(new File(args[0]));
        }
        return QuizLoader.loadFromClasspath("/questions.properties");
    }

    /**
     * 出題ループ本体。{@link Scanner}/{@link PrintStream} を引数として受け取ることで、
     * テストから {@code StringReader}/{@code ByteArrayOutputStream} を注入できるようにしている。
     * 出題順はquestionsに渡された順のまま(シャッフルしない)。
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
            out.println(correct ? "正解!" : "不正解(正解: " + question.getAnswer() + ")");
        }

        out.printf(
                "結果: %d/%d (%.1f%%)%n",
                scoreManager.getScore(), scoreManager.getTotal(), scoreManager.getPercentage());
    }

    /**
     * {@link #run(Scanner, PrintStream, List)}と同様だが、questionsのコピーを{@code random}で
     * シャッフルしてから出題する(呼び出し側のリストは変更しない)。
     * @param scanner 入力読み取り元
     * @param out 出力先
     * @param questions 出題する問題一覧
     * @param random 出題順のシャッフルに使う乱数(テストでは固定シードを渡して決定的に検証できる)
     */
    static void run(Scanner scanner, PrintStream out, List<Question> questions, Random random) {
        List<Question> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled, random);
        run(scanner, out, shuffled);
    }
}
