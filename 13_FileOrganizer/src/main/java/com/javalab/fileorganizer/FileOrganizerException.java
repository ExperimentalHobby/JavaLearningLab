package com.javalab.fileorganizer;

/**
 * 指定パスが存在しない、またはディレクトリでない場合など、ファイル整理ツール固有のエラーを表す例外。
 */
public class FileOrganizerException extends RuntimeException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public FileOrganizerException(String message) {
        super(message);
    }
}
