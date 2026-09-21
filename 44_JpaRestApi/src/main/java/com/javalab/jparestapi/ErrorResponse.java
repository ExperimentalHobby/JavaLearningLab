package com.javalab.jparestapi;

/**
 * フィールドに紐付かない単一のエラーメッセージを表すレスポンス。
 * @param message エラーメッセージ
 */
public record ErrorResponse(String message) {
}
