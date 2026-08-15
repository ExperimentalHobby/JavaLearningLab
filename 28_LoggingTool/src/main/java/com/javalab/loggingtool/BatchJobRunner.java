package com.javalab.loggingtool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ジョブ名の一覧をバッチ処理し、処理経過をログレベルに応じて出力する。
 * ロギング設計の題材として、開始/完了はINFO、詳細経過はDEBUG、
 * 不正入力のスキップはWARN、処理失敗はERRORという使い分けを実践する。
 */
public class BatchJobRunner {

    private static final Logger logger = LoggerFactory.getLogger(BatchJobRunner.class);

    /**
     * ジョブ名の一覧を順に処理する。
     * @param jobNames 処理対象のジョブ名一覧
     * @return 成功/失敗/スキップの件数集計
     */
    public BatchResult run(List<String> jobNames) {
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
                logger.error("ジョブ処理に失敗しました: {}", jobName, new JobExecutionException(jobName));
                failed++;
                continue;
            }
            succeeded++;
        }

        logger.info("バッチ処理が完了しました: 成功={} 失敗={} スキップ={}", succeeded, failed, skipped);
        return new BatchResult(succeeded, failed, skipped);
    }
}
