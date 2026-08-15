package com.javalab.completablefuturedemo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
