package com.javalab.jdbccrud;

/**
 * SQL関連の失敗を統一して表す非チェック例外。
 */
public class TaskRepositoryException extends RuntimeException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public TaskRepositoryException(String message) {
        super(message);
    }

    public TaskRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
