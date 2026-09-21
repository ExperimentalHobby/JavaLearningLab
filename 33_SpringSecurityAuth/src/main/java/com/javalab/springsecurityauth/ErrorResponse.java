package com.javalab.springsecurityauth;

/**
 * エラーレスポンスのボディ。
 * @param message エラーメッセージ
 */
public record ErrorResponse(String message) {
}
