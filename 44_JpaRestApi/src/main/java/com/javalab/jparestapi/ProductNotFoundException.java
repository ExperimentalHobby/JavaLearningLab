package com.javalab.jparestapi;

/**
 * 指定IDの商品が存在しない場合にスローする非チェック例外。
 * レスポンスへの変換(404・メッセージ)は{@link GlobalExceptionHandler}が行う。
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("該当する商品が見つかりません: id=" + id);
    }
}
