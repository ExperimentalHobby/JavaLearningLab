package com.javalab.completablefuturedemo;

/**
 * 実際の店舗API呼び出しの代わりに、指定した遅延で価格応答をシミュレートする実装。
 * ネットワークI/Oをモックせず、実際に{@link Thread#sleep(long)}で待つことで
 * 並行実行の効果(所要時間の短縮)をテストで検証できるようにしている。
 */
public class SimulatedShopPriceFetcher implements ShopPriceFetcher {

    private final String shopName;
    private final int price;
    private final long delayMillis;
    private final boolean shouldFail;

    public SimulatedShopPriceFetcher(String shopName, int price, long delayMillis, boolean shouldFail) {
        this.shopName = shopName;
        this.price = price;
        this.delayMillis = delayMillis;
        this.shouldFail = shouldFail;
    }

    @Override
    public String shopName() {
        return shopName;
    }

    @Override
    public int fetchPrice(String productName) throws InterruptedException {
        Thread.sleep(delayMillis);
        if (shouldFail) {
            throw new ShopUnavailableException(shopName);
        }
        return price;
    }
}
