package com.javalab.loggingtool;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link BatchJobRunner} のログレベル使い分け(開始/完了=INFO、詳細=DEBUG、
 * スキップ=WARN、失敗=ERROR)を検証するテスト。
 * モックを使わず、Logback標準の{@link ListAppender}を実際のLoggerに取り付けて
 * 実ログイベントの内容(レベル・メッセージ・例外情報)を直接検証する。
 */
class BatchJobRunnerTest {

    private Logger logger;
    private ListAppender<ILoggingEvent> appender;
    private BatchJobRunner runner;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(BatchJobRunner.class);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        runner = new BatchJobRunner();
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
    }

    @Test
    void runLogsInfoOnStartAndCompletionForNormalJob() {
        runner.run(List.of("job1"));

        List<String> infoMessages = appender.list.stream()
                .filter(event -> event.getLevel() == ch.qos.logback.classic.Level.INFO)
                .map(ILoggingEvent::getFormattedMessage)
                .toList();

        assertTrue(infoMessages.stream().anyMatch(m -> m.contains("開始")));
        assertTrue(infoMessages.stream().anyMatch(m -> m.contains("完了")));
    }

    @Test
    void runLogsWarnAndSkipsBlankJobName() {
        BatchResult result = runner.run(List.of(" "));

        boolean hasWarn = appender.list.stream()
                .anyMatch(event -> event.getLevel() == ch.qos.logback.classic.Level.WARN);
        assertTrue(hasWarn);
        assertEquals(1, result.skipped());
    }

    @Test
    void runLogsErrorWithExceptionInfoWhenJobNameStartsWithFail() {
        BatchResult result = runner.run(List.of("FAIL_job1"));

        boolean hasErrorWithException = appender.list.stream()
                .anyMatch(event -> event.getLevel() == ch.qos.logback.classic.Level.ERROR
                        && event.getThrowableProxy() != null);
        assertTrue(hasErrorWithException);
        assertEquals(1, result.failed());
    }

    @Test
    void runAggregatesCountsForMixedJobList() {
        BatchResult result = runner.run(List.of("job1", "job2", " ", "FAIL_job3"));

        assertEquals(2, result.succeeded());
        assertEquals(1, result.failed());
        assertEquals(1, result.skipped());
    }

    @Test
    void runAttachesSameBatchIdToAllLogEventsViaMdc() {
        // MDC(Mapped Diagnostic Context)でリクエストID(ここではバッチID)を相関させる題材。
        // 1回のrun()呼び出し中に出力される全ログイベントが同じbatchIdを持つことを確認する。
        runner.run(List.of("job1", "FAIL_job2"));

        List<String> batchIds = appender.list.stream()
                .map(event -> event.getMDCPropertyMap().get("batchId"))
                .distinct()
                .toList();

        assertEquals(1, batchIds.size());
        assertTrue(batchIds.get(0) != null && !batchIds.get(0).isEmpty());
    }

    @Test
    void runDoesNotLeakBatchIdIntoMdcAfterCompletion() {
        // finallyでMDC.remove()されており、run()完了後に他の処理へbatchIdが漏れ出さないことを確認する。
        runner.run(List.of("job1"));

        assertEquals(null, org.slf4j.MDC.get("batchId"));
    }

    @Test
    void runMarksFailureLogsWithJobFailureMarker() {
        // マーカーを使い、失敗ログだけを他のログと区別できることを確認する
        // (例: マーカーでフィルタして障害通知の対象を絞り込む、といった用途を想定)。
        runner.run(List.of("FAIL_job1"));

        boolean hasFailureMarker = appender.list.stream()
                .filter(event -> event.getLevel() == ch.qos.logback.classic.Level.ERROR)
                .anyMatch(event -> event.getMarkerList() != null
                        && event.getMarkerList().stream().anyMatch(m -> m.contains("JOB_FAILURE")));
        assertTrue(hasFailureMarker);
    }
}
