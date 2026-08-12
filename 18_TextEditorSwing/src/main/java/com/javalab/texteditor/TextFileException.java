package com.javalab.texteditor;

/**
 * ファイルI/O関連の失敗を統一して表す非チェック例外。
 */
public class TextFileException extends RuntimeException {

    public TextFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
