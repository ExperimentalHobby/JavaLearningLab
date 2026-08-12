package com.javalab.bookapi;

/**
 * 書籍作成APIのリクエストボディ。
 * @param title タイトル
 * @param author 著者
 */
public record BookRequest(String title, String author) {
}
