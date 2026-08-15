package com.javalab.completablefuturedemo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link SimulatedShopPriceFetcher} の遅延シミュレート・失敗シミュレートを検証するテスト。
 */
class SimulatedShopPriceFetcherTest {

    @Test
    void fetchPriceReturnsConfiguredPriceAfterDelay() throws Exception {
        SimulatedShopPriceFetcher fetcher = new SimulatedShopPriceFetcher("ShopA", 1000, 10, false);

        int price = fetcher.fetchPrice("ノートPC");

        assertEquals(1000, price);
    }

    @Test
    void fetchPriceThrowsExceptionWhenShouldFailIsTrue() {
        SimulatedShopPriceFetcher fetcher = new SimulatedShopPriceFetcher("ShopB", 500, 10, true);

        assertThrows(ShopUnavailableException.class, () -> fetcher.fetchPrice("ノートPC"));
    }
}
