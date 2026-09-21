package com.javalab.completablefuturedemo;

/**
 * 店舗から商品価格を取得するインターフェース。
 * 実装はI/O待ちを伴う可能性があるため、呼び出し元(PriceComparisonService)が
 * {@link java.util.concurrent.CompletableFuture#supplyAsync}経由で非同期実行する。
 */
public interface ShopPriceFetcher {

    /**
     * @return 店舗名
     */
    String shopName();

    /**
     * 指定商品の価格を取得する。
     * @param productName 商品名
     * @return 価格
     * @throws ShopFetchException 取得処理中に例外が発生した場合(通信断・割り込み等)
     */
    int fetchPrice(String productName) throws ShopFetchException;
}
