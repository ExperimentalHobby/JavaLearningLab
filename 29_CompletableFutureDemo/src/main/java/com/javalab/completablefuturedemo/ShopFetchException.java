package com.javalab.completablefuturedemo;

/**
 * 店舗からの価格取得処理そのものの失敗(通信断・タイムアウトによる中断等)を表す検査例外。
 * {@link ShopPriceFetcher#fetchPrice(String)}が汎用的な{@code Exception}を宣言していると
 * 呼び出し側が何を捕捉すべきか分からなくなるため、専用の例外型に絞っている。
 */
public class ShopFetchException extends Exception {

    public ShopFetchException(String shopName, Throwable cause) {
        super("店舗からの価格取得処理中に例外が発生しました: " + shopName, cause);
    }
}
