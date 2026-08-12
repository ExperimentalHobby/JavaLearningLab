package com.javalab.budget;

/**
 * 金額が0以下など、家計簿エントリとして不正な入力の場合の例外。
 */
public class BudgetException extends RuntimeException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public BudgetException(String message) {
        super(message);
    }
}
