package com.javalab.bank;

/**
 * 入出金額が0以下など、金額として不正な場合の例外。
 */
public class InvalidAmountException extends BankAccountException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public InvalidAmountException(String message) {
        super(message);
    }
}
