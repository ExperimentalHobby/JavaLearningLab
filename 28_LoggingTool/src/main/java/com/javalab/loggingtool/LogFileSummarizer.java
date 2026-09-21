package com.javalab.loggingtool;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * logback.xmlのFILEアペンダが出力したログファイルを読み込み、レベル別の件数を集計する。
 * フォルダ名・READMEの「ログ収集&解析ツール」に反して実際には収集も解析もしていなかった
 * 問題への対応。{@code 19_LogAnalyzerRegex}は任意形式のログファイルを汎用的にパースする
 * 学習教材だが、こちらはこのツール自身が出力するlogback.xmlのパターン専用の
 * 集計機能として役割を分けている。
 */
public class LogFileSummarizer {

    // logback.xmlのFILEアペンダのパターン
    // (%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{batchId}] - %msg%n)の
    // 先頭部分にマッチさせ、レベル名(%-5levelは右側を空白埋めするがtrimしなくても\Sで止まる)を抽出する。
    private static final Pattern LOG_LINE_PATTERN =
            Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} \\[[^\\]]*] (\\S+)");

    /**
     * ログファイルをレベルごとの行数に集計する。例外のスタックトレース等、
     * ログ行パターンに一致しない行(継続行)は集計対象外とする。
     * @param logFile 集計対象のログファイル
     * @return レベル名(TRACE/DEBUG/INFO/WARN/ERROR)ごとの件数(レベル名の辞書順)
     * @throws UncheckedIOException 読み込みに失敗した場合
     */
    public Map<String, Long> summarize(Path logFile) {
        Map<String, Long> counts = new TreeMap<>();
        try (Stream<String> lines = Files.lines(logFile)) {
            lines.forEach(line -> {
                Matcher matcher = LOG_LINE_PATTERN.matcher(line);
                if (matcher.find()) {
                    counts.merge(matcher.group(1), 1L, Long::sum);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException("ログファイルの読み込みに失敗しました: " + logFile, e);
        }
        return counts;
    }
}
