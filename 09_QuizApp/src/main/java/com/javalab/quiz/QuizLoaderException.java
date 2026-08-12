package com.javalab.quiz;

/**
 * 問題データのプロパティファイルが不正(必須キーの欠落など)な場合の例外。
 */
public class QuizLoaderException extends RuntimeException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public QuizLoaderException(String message) {
        super(message);
    }
}
