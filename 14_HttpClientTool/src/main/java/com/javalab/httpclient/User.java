package com.javalab.httpclient;

/**
 * JSON APIから取得したユーザー情報を表す不変な値オブジェクト。
 * @param id ユーザーID
 * @param name ユーザー名
 * @param email メールアドレス
 */
public record User(int id, String name, String email) {
}
