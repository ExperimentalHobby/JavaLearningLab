package com.javalab.beanvalidationapi;

/**
 * 登録済みユーザーを表す不変な値オブジェクト。REST APIのレスポンスにもそのまま使用する。
 */
public record User(long id, String name, String email, int age) {
}
