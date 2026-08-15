package com.javalab.loggingtool;

/**
 * ジョブ処理の失敗を表す非チェック例外。
 */
public class JobExecutionException extends RuntimeException {

    public JobExecutionException(String jobName) {
        super("ジョブの実行に失敗しました: " + jobName);
    }
}
