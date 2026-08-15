package com.javalab.loganalyzer;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link LogParser} の正規表現によるログ行解析・複数行一括解析・レベル別集計を検証するテスト。
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
    void parseAllSkipsMalformedLines() {
        // 3行中1行が不正な形式だが、parseAll()は例外を投げず不正な行だけをスキップし、
        // 残り2件が元の順序を保ったまま結果に含まれることを確認する。
        List<LogEntry> entries = parser.parseAll(List.of(
                "2026-08-13 10:15:30 [ERROR] データベース接続に失敗しました",
                "これはログ行ではありません",
                "2026-08-13 10:16:00 [INFO] 処理を開始しました"));

        assertEquals(2, entries.size());
        assertEquals("ERROR", entries.get(0).level());
        assertEquals("INFO", entries.get(1).level());
    }

    @Test
    void countByLevelAggregatesEntryCountPerLevel() {
        // ERROR2件・INFO1件という構成で、レベルごとの件数が正しく集計されることを確認する。
        List<LogEntry> entries = parser.parseAll(List.of(
                "2026-08-13 10:15:30 [ERROR] エラー1",
                "2026-08-13 10:15:31 [ERROR] エラー2",
                "2026-08-13 10:15:32 [INFO] 情報1"));

        Map<String, Long> counts = parser.countByLevel(entries);

        assertEquals(2L, counts.get("ERROR"));
        assertEquals(1L, counts.get("INFO"));
    }
}
