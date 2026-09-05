package com.javalab.beanvalidationapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 指定IDのユーザーが存在しない場合にスローする非チェック例外。
 * {@code @ResponseStatus}により、Spring MVCが自動的に404 Not Foundへマッピングする。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long id) {
        super("user not found: id=" + id);
    }
}
