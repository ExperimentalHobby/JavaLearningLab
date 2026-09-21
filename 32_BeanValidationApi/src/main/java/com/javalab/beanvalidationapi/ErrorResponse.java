package com.javalab.beanvalidationapi;

/**
 * フィールドに紐付かない単一のエラーメッセージを表すレスポンス。
 * @param message エラーメッセージ
 */
public record ErrorResponse(String message) {
}
