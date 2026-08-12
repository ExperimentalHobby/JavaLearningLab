package com.javalab.loganalyzer;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
     * コマンド: {@code load <ファイルパス>}(解析結果・レベル別集計を表示) / {@code exit}。
     * ファイル読み込みエラーはループを止めずエラー表示のみ行い、次のコマンド入力を継続する。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        LogParser parser = new LogParser();
        out.println("正規表現ログ解析ツール。コマンド: load <ファイルパス> / exit");
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
                handleLoad(parser, parts[1], out);
            } else {
                out.println("不明なコマンドです: " + line);
            }
        }
    }

    private static void handleLoad(LogParser parser, String filePath, PrintStream out) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            out.println("エラー: ファイルの読み込みに失敗しました: " + e.getMessage());
            return;
        }
        List<LogEntry> entries = parser.parseAll(lines);
        int skipped = lines.size() - entries.size();
        out.println("解析成功: " + entries.size() + "件(スキップ: " + skipped + "件)");
        Map<String, Long> counts = parser.countByLevel(entries);
        counts.forEach((level, count) -> out.println(level + ": " + count + "件"));
    }
}
