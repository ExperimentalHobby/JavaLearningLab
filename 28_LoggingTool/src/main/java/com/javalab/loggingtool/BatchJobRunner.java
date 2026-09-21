package com.javalab.loggingtool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.List;
import java.util.UUID;

/**
 * ジョブ名の一覧をバッチ処理し、処理経過をログレベルに応じて出力する。
 * ロギング設計の題材として、開始/完了はINFO、詳細経過はDEBUG、
 * 不正入力のスキップはWARN、処理失敗はERRORという使い分けを実践する。
 * さらにMDC(実行単位=バッチのID相関)とマーカー(失敗ログの識別)も実践している。
 */
public class BatchJobRunner {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobRunner.class);
    private static final Marker JOB_FAILURE_MARKER = MarkerFactory.getMarker("JOB_FAILURE");

    /**
     * ジョブ名の一覧を順に処理する。
     * @param jobNames 処理対象のジョブ名一覧
     * @return 成功/失敗/スキップの件数集計
     */
    public BatchResult run(List<String> jobNames) {
        // MDC(Mapped Diagnostic Context)にバッチIDを積むことで、1回のrun()呼び出し中に
        // 出力される全ログイベントを後から相関させられるようにする(複数バッチが並行実行される
        // 環境でログを追う際、どのログ行がどのバッチ実行に属するかを判別する手がかりになる)。
        String batchId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("batchId", batchId);
        try {
            logger.info("バッチ処理を開始します: 件数={}", jobNames.size());

            int succeeded = 0;
            int failed = 0;
            int skipped = 0;
            for (String jobName : jobNames) {
                if (jobName.isBlank()) {
                    logger.warn("ジョブ名が空のためスキップします");
                    skipped++;
                    continue;
                }
                logger.debug("ジョブ処理開始: {}", jobName);
                if (jobName.startsWith("FAIL_")) {
                    // マーカーで失敗ログを識別可能にする(例: マーカーでフィルタして障害通知先へ
                    // 転送する、といったAppender側の振り分けに使える)。
                    logger.error(JOB_FAILURE_MARKER, "ジョブ処理に失敗しました: {}", jobName,
                            new JobExecutionException(jobName));
                    failed++;
                    continue;
                }
                succeeded++;
            }

            logger.info("バッチ処理が完了しました: 成功={} 失敗={} スキップ={}", succeeded, failed, skipped);
            return new BatchResult(succeeded, failed, skipped);
        } finally {
            // run()完了後に他の処理へbatchIdが漏れ出さないよう、必ず除去する。
            MDC.remove("batchId");
        }
    }
}
