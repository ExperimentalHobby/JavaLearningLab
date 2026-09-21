package com.javalab.junitpractice;

/**
 * メールアドレスの形式検証を表すインターフェース。{@link EmailSender}と同じくコンストラクタ注入し、
 * テストではMockitoで{@code when().thenReturn()}によりスタブ化できるようにする。
 */
public interface EmailValidator {

    boolean isValid(String email);
}
