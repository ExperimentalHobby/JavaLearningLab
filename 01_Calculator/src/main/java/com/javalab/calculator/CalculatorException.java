package com.javalab.calculator;

/**
 * 不正な演算子や不正な入力形式など、電卓アプリ固有のエラーを表す例外。
 */
public class CalculatorException extends RuntimeException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public CalculatorException(String message) {
        super(message);
    }
}
