package com.javalab.completablefuturedemo;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void fetchPriceWrapsInterruptedExceptionIntoShopFetchExceptionAndRestoresInterruptFlag() throws Exception {
        // Thread.sleep()中に割り込まれた場合、ShopPriceFetcher.fetchPrice()のthrows Exceptionが
        // 広すぎる問題への対応として導入した専用の検査例外(ShopFetchException)にラップし、
        // 割り込みフラグ自体は呼び出し元が検知できるよう復元しておく必要がある。
        SimulatedShopPriceFetcher fetcher = new SimulatedShopPriceFetcher("ShopC", 1000, 5000, false);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch started = new CountDownLatch(1);
        AtomicBoolean interruptedFlagRestored = new AtomicBoolean(false);

        Future<?> future = executor.submit(() -> {
            started.countDown();
            try {
                fetcher.fetchPrice("ノートPC");
            } catch (ShopFetchException e) {
                interruptedFlagRestored.set(Thread.currentThread().isInterrupted());
            } catch (Exception e) {
                throw new AssertionError("ShopFetchExceptionが送出されるはず", e);
            }
        });

        assertTrue(started.await(2, TimeUnit.SECONDS));
        Thread.sleep(50);
        future.cancel(true);
        executor.shutdown();
        assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
        assertTrue(interruptedFlagRestored.get());
    }
}
