package com.javalab.jparestapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 注文数量が在庫を上回る場合にスローする非チェック例外。 */
@ResponseStatus(HttpStatus.CONFLICT)
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId) {
        super("insufficient stock: productId=" + productId);
    }
}
