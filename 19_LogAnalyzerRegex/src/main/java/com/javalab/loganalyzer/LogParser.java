package com.javalab.loganalyzer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@code yyyy-MM-dd HH:mm:ss [レベル] メッセージ}形式のログ行を{@link Pattern}/{@link Matcher}で解析するクラス。
 */
public class LogParser {

    private static final Pattern LOG_PATTERN =
            Pattern.compile("^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) \\[(\\w+)\\] (.+)$");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 1行を解析し{@link LogEntry}に変換する。
     * @param line 解析対象のログ行
     * @return 解析結果
     * @throws LogParseException 行の形式が期待するパターンに合わない場合、または日時が実在しない場合
     */
    public LogEntry parse(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (!matcher.matches()) {
            throw new LogParseException("ログ行の形式が不正です: " + line);
        }
        LocalDateTime timestamp;
        try {
            timestamp = LocalDateTime.parse(matcher.group(1), TIMESTAMP_FORMAT);
        } catch (DateTimeParseException e) {
            // "2026-13-45 99:99:99"のように桁数は正規表現に一致するが実在しない日時の場合、
            // DateTimeParseException(LogParseExceptionではない)が投げられ、parseAllの
            // スキップ処理をすり抜けてアプリ全体が落ちていたため、LogParseExceptionに変換する。
            throw new LogParseException("ログ行の日時が不正です: " + line);
        }
        String level = matcher.group(2);
        String message = matcher.group(3);
        return new LogEntry(timestamp, level, message);
    }

    /**
     * 複数行を解析する。形式に合わない行は例外を投げずスキップし、{@link ParseResult#skipped()}に記録する。
     * @param lines 解析対象の行一覧
     * @return 解析結果(成功分・スキップ分の両方)
     */
    public ParseResult parseAll(List<String> lines) {
        return parseAll(lines.stream());
    }

    /**
     * {@link #parseAll(List)}と同様だが、{@link Stream}を受け取ることで呼び出し側が
     * (例えば{@code Files.lines}のように)全行を事前にメモリへ読み込まずに済むようにしている。
     * @param lines 解析対象の行のストリーム
     * @return 解析結果(成功分・スキップ分の両方)
     */
    public ParseResult parseAll(Stream<String> lines) {
        List<LogEntry> entries = new ArrayList<>();
        List<SkippedLine> skipped = new ArrayList<>();
        // 実際のログファイルには形式に合わない行(スタックトレースの継続行等)が
        // 混ざりうるため、1行の解析失敗で全体を止めずスキップして処理を継続する。
        // どの行が・なぜスキップされたか追えるよう、行番号(1始まり)と理由を記録する。
        int[] lineNumber = {0};
        lines.forEach(line -> {
            lineNumber[0]++;
            try {
                entries.add(parse(line));
            } catch (LogParseException e) {
                skipped.add(new SkippedLine(lineNumber[0], line, e.getMessage()));
            }
        });
        return new ParseResult(entries, skipped);
    }

    /**
     * ログレベルごとの件数を集計する。
     * @param entries 集計対象のログ一覧
     * @return レベル文字列(昇順)をキーとした件数マップ
     */
    public Map<String, Long> countByLevel(List<LogEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(
                        LogEntry::level,
                        // Collectors.groupingByの既定(HashMap)だと表示順が実行のたびに変わりうるため、
                        // TreeMapを明示する(12_StreamApiPracticeと同じ方針)。
                        TreeMap::new,
                        Collectors.counting()));
    }

    /**
     * 指定レベルのログのみを抽出する。
     * @param entries 絞り込み対象のログ一覧
     * @param level 抽出対象のレベル(例: "ERROR")
     * @return levelに一致するログのみを元の順序で含むリスト
     */
    public List<LogEntry> filterByLevel(List<LogEntry> entries, String level) {
        return entries.stream()
                .filter(entry -> entry.level().equals(level))
                .toList();
    }

    /**
     * 指定期間(両端含む)のログのみを抽出する。
     * @param entries 絞り込み対象のログ一覧
     * @param from 期間の開始日時
     * @param to 期間の終了日時
     * @return 期間内のログのみを元の順序で含むリスト
     */
    public List<LogEntry> filterByPeriod(List<LogEntry> entries, LocalDateTime from, LocalDateTime to) {
        return entries.stream()
                .filter(entry -> !entry.timestamp().isBefore(from) && !entry.timestamp().isAfter(to))
                .toList();
    }

    /**
     * {@link #parseAll(List)}/{@link #parseAll(Stream)}の結果。
     * @param entries 解析に成功したログ一覧(元の行の順序を維持)
     * @param skipped 解析に失敗しスキップされた行一覧(行番号・元の行内容・理由)
     */
    public record ParseResult(List<LogEntry> entries, List<SkippedLine> skipped) {
    }

    /**
     * 解析に失敗しスキップされた1行の情報。
     * @param lineNumber 元のファイル内での行番号(1始まり)
     * @param line 元の行の内容
     * @param reason スキップされた理由(解析時の例外メッセージ)
     */
    public record SkippedLine(int lineNumber, String line, String reason) {
    }
}
