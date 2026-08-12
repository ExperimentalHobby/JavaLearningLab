package com.javalab.bookapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 指定IDの書籍が存在しない場合の非チェック例外。
 * {@code @ResponseStatus}により、Spring MVCがこの例外を自動的にHTTP 404へマッピングする。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(long id) {
        super("書籍が見つかりません: id=" + id);
    }
}
