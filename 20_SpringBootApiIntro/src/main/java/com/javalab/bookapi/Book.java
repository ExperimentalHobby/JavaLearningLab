package com.javalab.bookapi;

/**
 * 書籍1件を表す不変な値オブジェクト。
 * @param id 書籍ID(サーバー側で採番)
 * @param title タイトル
 * @param author 著者
 */
public record Book(long id, String title, String author) {
}
