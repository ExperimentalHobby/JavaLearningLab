package com.javalab.junitpractice;

/**
 * 注文通知メールの送信失敗を統一して表す非チェック例外。
 */
public class OrderNotificationException extends RuntimeException {

    public OrderNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
