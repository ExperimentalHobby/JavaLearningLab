package com.javalab.beanvalidationapi;

/**
 * Bean Validationの検証エラー1件分を表すレスポンス要素。
 * @param field 検証に失敗したフィールド名
 * @param message エラーメッセージ
 */
public record FieldErrorResponse(String field, String message) {
}
