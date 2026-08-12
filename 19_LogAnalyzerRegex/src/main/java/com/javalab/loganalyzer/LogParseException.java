package com.javalab.loganalyzer;

/**
 * ログ行の解析失敗を統一して表す非チェック例外。
 */
public class LogParseException extends RuntimeException {

    public LogParseException(String message) {
        super(message);
    }
}
