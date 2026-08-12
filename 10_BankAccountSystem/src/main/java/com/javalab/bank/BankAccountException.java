package com.javalab.bank;

/**
 * 口座操作に関する独自例外の基底クラス。{@link InvalidAmountException}・{@link InsufficientBalanceException}が
 * これを継承することで、呼び出し側は個別の原因を区別せず一括で捕捉することもできる。
 */
public abstract class BankAccountException extends RuntimeException {

    protected BankAccountException(String message) {
        super(message);
    }
}
