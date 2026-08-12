package com.javalab.bank;

/**
 * 出金額が残高を超えている場合の例外。
 */
public class InsufficientBalanceException extends BankAccountException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
