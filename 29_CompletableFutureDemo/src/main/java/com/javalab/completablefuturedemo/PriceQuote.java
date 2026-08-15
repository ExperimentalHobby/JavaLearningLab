package com.javalab.completablefuturedemo;

/**
 * 1店舗分の価格見積もり結果。
 * @param shopName 店舗名
 * @param price 価格。取得できなかった場合はnull
 * @param status 取得結果の状態
 */
public record PriceQuote(String shopName, Integer price, Status status) {

    /** 見積もりの取得結果を表す状態。 */
    public enum Status {
        OK, FAILED, TIMEOUT
    }

    public static PriceQuote ok(String shopName, int price) {
        return new PriceQuote(shopName, price, Status.OK);
    }

    public static PriceQuote failed(String shopName) {
        return new PriceQuote(shopName, null, Status.FAILED);
    }

    public static PriceQuote timeout(String shopName) {
        return new PriceQuote(shopName, null, Status.TIMEOUT);
    }
}
