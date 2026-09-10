package com.javalab.jparestapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 指定IDの商品が存在しない場合にスローする非チェック例外。 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("product not found: id=" + id);
    }
}
