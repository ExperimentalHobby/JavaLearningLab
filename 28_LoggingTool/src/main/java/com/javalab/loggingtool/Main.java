package com.javalab.loggingtool;

import ch.qos.logback.classic.Level;

import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
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
        out.println("ログ収集&解析ツール。コマンド: run <job1,job2,...> / level <ロガー名> <LEVEL> / "
                + "summary [ログファイルパス] / exit");
        LogFileSummarizer summarizer = new LogFileSummarizer();
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
                    case "summary" -> handleSummary(summarizer, parts, out);
                    default -> out.println("不明なコマンドです: " + line);
                }
            } catch (IllegalArgumentException | UncheckedIOException e) {
                // IllegalArgumentExceptionは引数検証・不正なログレベル、
                // UncheckedIOExceptionはsummaryコマンドのログファイル読み込み失敗から送出される。
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleRun(BatchJobRunner jobRunner, String[] parts, PrintStream out) {
        if (parts.length < 2) {
            throw new IllegalArgumentException("使用方法: run <job1,job2,...>");
        }
        List<String> jobNames = List.of(parts[1].split(","));
        BatchResult result = jobRunner.run(jobNames);
        out.println("成功=" + result.succeeded() + " 失敗=" + result.failed() + " スキップ=" + result.skipped());
    }

    private static void handleLevel(LogLevelController levelController, String[] parts, PrintStream out) {
        String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];
        if (args.length != 2) {
            throw new IllegalArgumentException("使用方法: level <ロガー名> <LEVEL>");
        }
        String loggerName = args[0];
        // Level.valueOf()は不明な文字列に対して例外を投げずDEBUGを返す仕様のため、
        // Level.toLevel(name, null)で検証し、nullが返る(=未知のレベル名)場合は明示的にエラーとする。
        Level level = Level.toLevel(args[1], null);
        if (level == null) {
            throw new IllegalArgumentException("不正なログレベルです: " + args[1]
                    + "(TRACE/DEBUG/INFO/WARN/ERROR/OFFのいずれかを指定してください)");
        }
        levelController.setLevel(loggerName, level);
        out.println("ログレベルを変更しました: " + loggerName + " -> " + level);
    }

    private static void handleSummary(LogFileSummarizer summarizer, String[] parts, PrintStream out) {
        // 引数省略時はlogback.xmlのFILEアペンダの既定出力先(logs/app.log)を対象にする。
        String path = parts.length > 1 ? parts[1].trim() : "logs/app.log";
        Map<String, Long> counts = summarizer.summarize(Path.of(path));
        if (counts.isEmpty()) {
            out.println("集計対象のログ行がありません: " + path);
            return;
        }
        counts.forEach((level, count) -> out.println(level + ": " + count));
    }
}
