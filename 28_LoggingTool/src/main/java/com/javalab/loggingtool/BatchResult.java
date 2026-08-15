package com.javalab.loggingtool;

/**
 * {@link BatchJobRunner}の実行結果集計。
 * @param succeeded 成功件数
 * @param failed 失敗件数
 * @param skipped スキップ件数
 */
public record BatchResult(int succeeded, int failed, int skipped) {
}
