package com.javalab.loganalyzer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * 正規表現ログ解析ツールのエントリーポイント。
 * 標準入力からコマンドを読み取り、ログファイルを解析して結果を表示する対話型REPLを提供する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}を差し替えられるよう分離している。
     * コマンド: {@code load <ファイルパス>}(解析結果・レベル別集計を表示) /
     * {@code filterByLevel <レベル>} / {@code errors}(ERRORレベルのみ抽出) /
     * {@code filterByPeriod <開始日時> <終了日時>}(ISO形式、例: 2026-08-13T10:00:00) / {@code exit}。
     * `filterByLevel`/`errors`/`filterByPeriod`は直近の{@code load}結果に対して絞り込む。
     * ファイル読み込みエラーはループを止めずエラー表示のみ行い、次のコマンド入力を継続する。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        LogParser parser = new LogParser();
        List<LogEntry> currentEntries = new ArrayList<>();
        out.println("正規表現ログ解析ツール。コマンド: load <ファイルパス> / filterByLevel <レベル> / "
                + "errors / filterByPeriod <開始日時> <終了日時> / exit");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+", 2);
            String command = parts[0];
            if (command.equals("exit")) {
                return;
            } else if (command.equals("load") && parts.length == 2) {
                currentEntries = handleLoad(parser, parts[1], out);
            } else if (command.equals("filterByLevel") && parts.length == 2) {
                printEntries(parser.filterByLevel(currentEntries, parts[1]), out);
            } else if (command.equals("errors")) {
                printEntries(parser.filterByLevel(currentEntries, "ERROR"), out);
            } else if (command.equals("filterByPeriod") && parts.length == 2) {
                handleFilterByPeriod(parser, currentEntries, parts[1], out);
            } else {
                out.println("不明なコマンドです: " + line);
            }
        }
    }

    private static List<LogEntry> handleLoad(LogParser parser, String filePath, PrintStream out) {
        // Files.readAllLinesで全行を一度にメモリへ載せると大きなログで問題になるため、
        // Files.lines(Stream)でストリーム処理し、LogParser.parseAll(Stream)へそのまま渡す。
        LogParser.ParseResult result;
        try (Stream<String> lines = Files.lines(Path.of(filePath), StandardCharsets.UTF_8)) {
            result = parser.parseAll(lines);
        } catch (IOException e) {
            out.println("エラー: ファイルの読み込みに失敗しました: " + e.getMessage());
            return List.of();
        }
        out.println("解析成功: " + result.entries().size() + "件(スキップ: " + result.skipped().size() + "件)");
        for (LogParser.SkippedLine skipped : result.skipped()) {
            out.println("  スキップ(" + skipped.lineNumber() + "行目): " + skipped.reason());
        }
        Map<String, Long> counts = parser.countByLevel(result.entries());
        counts.forEach((level, count) -> out.println(level + ": " + count + "件"));
        return result.entries();
    }

    private static void handleFilterByPeriod(
            LogParser parser, List<LogEntry> currentEntries, String args, PrintStream out) {
        String[] range = args.split("\\s+");
        if (range.length != 2) {
            out.println("使い方: filterByPeriod <開始日時> <終了日時>(例: 2026-08-13T10:00:00)");
            return;
        }
        try {
            LocalDateTime from = LocalDateTime.parse(range[0]);
            LocalDateTime to = LocalDateTime.parse(range[1]);
            printEntries(parser.filterByPeriod(currentEntries, from, to), out);
        } catch (DateTimeParseException e) {
            out.println("エラー: 日時の形式が不正です(例: 2026-08-13T10:00:00): " + e.getMessage());
        }
    }

    private static void printEntries(List<LogEntry> entries, PrintStream out) {
        if (entries.isEmpty()) {
            out.println("該当するログがありません");
            return;
        }
        for (LogEntry entry : entries) {
            out.println(entry.timestamp() + " [" + entry.level() + "] " + entry.message());
        }
    }
}
