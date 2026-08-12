package com.javalab.jdbccrud;

/**
 * SQL関連の失敗を統一して表す非チェック例外。
 */
public class TaskRepositoryException extends RuntimeException {

    public TaskRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
