package com.javalab.loganalyzer;

import java.time.LocalDateTime;

/**
 * 解析済みログ1件を表す不変な値オブジェクト。
 * @param timestamp ログ発生日時
 * @param level ログレベル(例: ERROR, WARN, INFO)
 * @param message メッセージ本文
 */
public record LogEntry(LocalDateTime timestamp, String level, String message) {
}
