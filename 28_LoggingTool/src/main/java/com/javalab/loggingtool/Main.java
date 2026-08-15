package com.javalab.loggingtool;

import ch.qos.logback.classic.Level;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * ログ収集&解析ツールのエントリーポイント。
 * {@code run}コマンドで{@link BatchJobRunner}によるバッチ処理を実行し、
 * {@code level}コマンドで{@link LogLevelController}により実行時にログレベルを変更する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out, new BatchJobRunner(), new LogLevelController());
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}/依存コンポーネントを
     * 差し替えられるよう分離している。
     * コマンド: {@code run <job1,job2,...>} / {@code level <ロガー名> <LEVEL>} / {@code exit}。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     * @param jobRunner バッチ処理を担うコンポーネント
     * @param levelController ログレベル変更を担うコンポーネント
     */
    static void run(Scanner scanner, PrintStream out, BatchJobRunner jobRunner, LogLevelController levelController) {
        out.println("ログ収集&解析ツール。コマンド: run <job1,job2,...> / level <ロガー名> <LEVEL> / exit");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+", 2);
            String command = parts[0];
            try {
                switch (command) {
                    case "exit" -> {
                        return;
                    }
                    case "run" -> handleRun(jobRunner, parts, out);
                    case "level" -> handleLevel(levelController, parts, out);
                    default -> out.println("不明なコマンドです: " + line);
                }
            } catch (RuntimeException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleRun(BatchJobRunner jobRunner, String[] parts, PrintStream out) {
        List<String> jobNames = List.of(parts[1].split(","));
        BatchResult result = jobRunner.run(jobNames);
        out.println("成功=" + result.succeeded() + " 失敗=" + result.failed() + " スキップ=" + result.skipped());
    }

    private static void handleLevel(LogLevelController levelController, String[] parts, PrintStream out) {
        String[] args = parts[1].split("\\s+");
        String loggerName = args[0];
        Level level = Level.valueOf(args[1]);
        levelController.setLevel(loggerName, level);
        out.println("ログレベルを変更しました: " + loggerName + " -> " + level);
    }
}
