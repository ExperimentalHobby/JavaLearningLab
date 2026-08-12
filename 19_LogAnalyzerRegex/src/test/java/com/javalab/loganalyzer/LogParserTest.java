package com.javalab.loganalyzer;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogParserTest {

    private final LogParser parser = new LogParser();

    @Test
    void parseExtractsTimestampLevelAndMessage() {
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
        List<LogEntry> entries = parser.parseAll(List.of(
                "2026-08-13 10:15:30 [ERROR] エラー1",
                "2026-08-13 10:15:31 [ERROR] エラー2",
                "2026-08-13 10:15:32 [INFO] 情報1"));

        Map<String, Long> counts = parser.countByLevel(entries);

        assertEquals(2L, counts.get("ERROR"));
        assertEquals(1L, counts.get("INFO"));
    }
}
