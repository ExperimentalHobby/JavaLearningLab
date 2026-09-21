package com.javalab.loggingtool;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link LogFileSummarizer} によるログファイルのレベル別集計を検証するテスト。
 * logback.xmlのFILEアペンダが実際に出力する形式の行を使い、正しくレベルを抽出できることを確認する。
 */
class LogFileSummarizerTest {

    private final LogFileSummarizer summarizer = new LogFileSummarizer();

    @Test
    void summarizeCountsLinesByLevel(@TempDir Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("app.log");
        Files.write(logFile, List.of(
                "2026-09-21 10:00:00.000 [main] INFO  c.javalab.loggingtool.BatchJobRunner [abc123] - バッチ処理を開始します: 件数=2",
                "2026-09-21 10:00:00.001 [main] DEBUG c.javalab.loggingtool.BatchJobRunner [abc123] - ジョブ処理開始: job1",
                "2026-09-21 10:00:00.002 [main] WARN  c.javalab.loggingtool.BatchJobRunner [abc123] - ジョブ名が空のためスキップします",
                "2026-09-21 10:00:00.003 [main] ERROR c.javalab.loggingtool.BatchJobRunner [abc123] - ジョブ処理に失敗しました: FAIL_job2",
                "2026-09-21 10:00:00.004 [main] INFO  c.javalab.loggingtool.BatchJobRunner [abc123] - バッチ処理が完了しました: 成功=1 失敗=1 スキップ=1"),
                StandardCharsets.UTF_8);

        Map<String, Long> counts = summarizer.summarize(logFile);

        assertEquals(2L, counts.get("INFO"));
        assertEquals(1L, counts.get("DEBUG"));
        assertEquals(1L, counts.get("WARN"));
        assertEquals(1L, counts.get("ERROR"));
    }

    @Test
    void summarizeIgnoresLinesThatDoNotMatchLogPattern(@TempDir Path tempDir) throws IOException {
        // 例外のスタックトレース行など、ログパターンに一致しない行(継続行)は集計対象外とする。
        Path logFile = tempDir.resolve("app.log");
        Files.write(logFile, List.of(
                "2026-09-21 10:00:00.000 [main] ERROR c.javalab.loggingtool.BatchJobRunner [abc123] - 失敗しました",
                "\tat com.javalab.loggingtool.BatchJobRunner.run(BatchJobRunner.java:42)",
                "Caused by: java.lang.RuntimeException"),
                StandardCharsets.UTF_8);

        Map<String, Long> counts = summarizer.summarize(logFile);

        assertEquals(1L, counts.get("ERROR"));
        assertEquals(1, counts.size());
    }

    @Test
    void summarizeThrowsUncheckedIOExceptionForNonExistentFile(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("does-not-exist.log");

        assertThrows(UncheckedIOException.class, () -> summarizer.summarize(missing));
    }
}
