package com.javalab.completablefuturedemo;

/**
 * 全店舗が失敗またはタイムアウトし、有効な見積もりが1件も無かったことを表す非チェック例外。
 */
public class NoAvailablePriceException extends RuntimeException {

    public NoAvailablePriceException(String productName) {
        super("入手可能な価格が見つかりませんでした: " + productName);
    }
}
