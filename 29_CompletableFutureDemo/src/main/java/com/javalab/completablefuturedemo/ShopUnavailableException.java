package com.javalab.completablefuturedemo;

/**
 * 店舗からの価格取得に失敗したことを表す非チェック例外。
 */
public class ShopUnavailableException extends RuntimeException {

    public ShopUnavailableException(String shopName) {
        super("店舗からの価格取得に失敗しました: " + shopName);
    }
}
