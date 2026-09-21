package com.javalab.loganalyzer;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link LogParser} の正規表現によるログ行解析・複数行一括解析・レベル別集計・絞り込みを検証するテスト。
 * 実際のログファイルには形式に合わない行(スタックトレースの継続行等)が混ざりうるため、
 * それをスキップして処理を継続できることも重要な検証対象になっている。
 */
class LogParserTest {

    private final LogParser parser = new LogParser();

    @Test
    void parseExtractsTimestampLevelAndMessage() {
        // "yyyy-MM-dd HH:mm:ss [レベル] メッセージ" の3グループが正しく抽出されることを確認する。
        LogEntry entry = parser.parse("2026-08-13 10:15:30 [ERROR] データベース接続に失敗しました");

        assertEquals(LocalDateTime.of(2026, 8, 13, 10, 15, 30), entry.timestamp());
        assertEquals("ERROR", entry.level());
        assertEquals("データベース接続に失敗しました", entry.message());
    }

    @Test
    void parseThrowsLogParseExceptionForMalformedLine() {
        assertThrows(LogParseException.class, () -> parser.parse("これはログ行ではありません"));
    }

    @Test
    void parseThrowsLogParseExceptionForNonExistentDateTime() {
        // "2026-13-45 99:99:99"は正規表現(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})には一致するが、
        // LocalDateTime.parseがDateTimeParseExceptionを投げる。これはLogParseExceptionではないため
        // parseAllのスキップ処理をすり抜け、Main.handleLoadでも捕捉されずREPLごと終了していた。
        assertThrows(LogParseException.class,
                () -> parser.parse("2026-13-45 99:99:99 [INFO] x"));
    }

    @Test
    void parseAllSkipsMalformedLines() {
        // 3行中1行が不正な形式だが、parseAll()は例外を投げず不正な行だけをスキップし、
        // 残り2件が元の順序を保ったまま結果に含まれることを確認する。
        LogParser.ParseResult result = parser.parseAll(List.of(
                "2026-08-13 10:15:30 [ERROR] データベース接続に失敗しました",
                "これはログ行ではありません",
                "2026-08-13 10:16:00 [INFO] 処理を開始しました"));

        assertEquals(2, result.entries().size());
        assertEquals("ERROR", result.entries().get(0).level());
        assertEquals("INFO", result.entries().get(1).level());
    }

    @Test
    void parseAllRecordsSkippedLinesWithLineNumberAndReason() {
        // 空のcatchブロックでスキップした行の内容・行番号が一切残らず、何が落ちたのか
        // 追えなかった問題への対応。行番号(1始まり)・元の行内容・理由が記録されることを確認する。
        LogParser.ParseResult result = parser.parseAll(List.of(
                "2026-08-13 10:15:30 [ERROR] エラー",
                "これはログ行ではありません"));

        assertEquals(1, result.skipped().size());
        LogParser.SkippedLine skipped = result.skipped().get(0);
        assertEquals(2, skipped.lineNumber());
        assertEquals("これはログ行ではありません", skipped.line());
        assertTrue(skipped.reason() != null && !skipped.reason().isBlank());
    }

    @Test
    void parseAllAcceptsStreamForMemoryEfficientProcessing() {
        // Files.readAllLinesで全行をメモリに載せていた問題への対応。
        // Stream<String>を受け取るオーバーロードでも同じ結果になることを確認する。
        LogParser.ParseResult result = parser.parseAll(List.of(
                "2026-08-13 10:15:30 [ERROR] エラー",
                "2026-08-13 10:16:00 [INFO] 情報").stream());

        assertEquals(2, result.entries().size());
    }

    @Test
    void countByLevelAggregatesEntryCountPerLevel() {
        // ERROR2件・INFO1件という構成で、レベルごとの件数が正しく集計されることを確認する。
        List<LogEntry> entries = parser.parseAll(List.of(
                "2026-08-13 10:15:30 [ERROR] エラー1",
                "2026-08-13 10:15:31 [ERROR] エラー2",
                "2026-08-13 10:15:32 [INFO] 情報1")).entries();

        Map<String, Long> counts = parser.countByLevel(entries);

        assertEquals(2L, counts.get("ERROR"));
        assertEquals(1L, counts.get("INFO"));
    }

    @Test
    void countByLevelReturnsMapWithDeterministicOrder() {
        // Collectors.groupingByの既定(HashMap)だと表示順が不定だった(12_StreamApiPracticeと同じ問題)。
        List<LogEntry> entries = parser.parseAll(List.of(
                "2026-08-13 10:15:30 [WARN] 警告",
                "2026-08-13 10:15:31 [ERROR] エラー",
                "2026-08-13 10:15:32 [INFO] 情報")).entries();

        Map<String, Long> counts = parser.countByLevel(entries);

        assertInstanceOf(TreeMap.class, counts);
        assertEquals(List.of("ERROR", "INFO", "WARN"), List.copyOf(counts.keySet()));
    }

    @Test
    void filterByLevelReturnsOnlyMatchingEntries() {
        // 「レベル絞り込み・期間指定・ERROR抽出といった解析機能がない」という学習テーマへの対応。
        List<LogEntry> entries = parser.parseAll(List.of(
                "2026-08-13 10:15:30 [ERROR] エラー",
                "2026-08-13 10:15:31 [INFO] 情報")).entries();

        List<LogEntry> errors = parser.filterByLevel(entries, "ERROR");

        assertEquals(1, errors.size());
        assertEquals("エラー", errors.get(0).message());
    }

    @Test
    void filterByPeriodReturnsOnlyEntriesWithinRangeInclusive() {
        List<LogEntry> entries = parser.parseAll(List.of(
                "2026-08-13 09:00:00 [INFO] 早すぎる",
                "2026-08-13 10:00:00 [INFO] 範囲内1",
                "2026-08-13 11:00:00 [INFO] 範囲内2",
                "2026-08-13 12:00:00 [INFO] 遅すぎる")).entries();

        List<LogEntry> filtered = parser.filterByPeriod(
                entries, LocalDateTime.of(2026, 8, 13, 10, 0, 0), LocalDateTime.of(2026, 8, 13, 11, 0, 0));

        assertEquals(2, filtered.size());
        assertEquals("範囲内1", filtered.get(0).message());
        assertEquals("範囲内2", filtered.get(1).message());
    }
}
