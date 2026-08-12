package com.javalab.loganalyzer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     * @throws LogParseException 行の形式が期待するパターンに合わない場合
     */
    public LogEntry parse(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (!matcher.matches()) {
            throw new LogParseException("ログ行の形式が不正です: " + line);
        }
        LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1), TIMESTAMP_FORMAT);
        String level = matcher.group(2);
        String message = matcher.group(3);
        return new LogEntry(timestamp, level, message);
    }

    /**
     * 複数行を解析する。形式に合わない行は例外を投げずスキップし、結果に含めない。
     * @param lines 解析対象の行一覧
     * @return 解析に成功した{@link LogEntry}の一覧(元の行の順序を維持)
     */
    public List<LogEntry> parseAll(List<String> lines) {
        List<LogEntry> entries = new ArrayList<>();
        for (String line : lines) {
            // 実際のログファイルには形式に合わない行(スタックトレースの継続行等)が
            // 混ざりうるため、1行の解析失敗で全体を止めずスキップして処理を継続する。
            try {
                entries.add(parse(line));
            } catch (LogParseException e) {
                // スキップ
            }
        }
        return entries;
    }

    /**
     * ログレベルごとの件数を集計する。
     * @param entries 集計対象のログ一覧
     * @return レベル文字列をキーとした件数マップ
     */
    public Map<String, Long> countByLevel(List<LogEntry> entries) {
        return entries.stream()
                .collect(Collectors.groupingBy(LogEntry::level, Collectors.counting()));
    }
}
