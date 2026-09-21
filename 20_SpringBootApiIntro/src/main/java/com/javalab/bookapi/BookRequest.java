package com.javalab.bookapi;

import jakarta.validation.constraints.NotBlank;

/**
 * 書籍作成・更新APIのリクエストボディ。
 * @param title タイトル。空文字不可
 * @param author 著者。空文字不可
 */
public record BookRequest(
        @NotBlank(message = "title must not be blank") String title,
        @NotBlank(message = "author must not be blank") String author) {
}
