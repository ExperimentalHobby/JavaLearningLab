package com.javalab.jparestapi;

/**
 * 注文数量が在庫を上回る場合にスローする非チェック例外。
 * レスポンスへの変換(409・メッセージ)は{@link GlobalExceptionHandler}が行う。
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId) {
        super("在庫が不足しています: 商品ID=" + productId);
    }
}
