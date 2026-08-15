package com.javalab.completablefuturedemo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link PriceComparisonService} のCompletableFuture非同期チェーン(supplyAsync/orTimeout/
 * exceptionally/allOf/thenApply)を検証するテスト。並行実行の効果は実測時間で検証し、
 * モックやタイマー操作は使用していない(実際に複数スレッドで待機させている)。
 */
class PriceComparisonServiceTest {

    private ExecutorService executor;
    private PriceComparisonService service;

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(4);
        service = new PriceComparisonService(executor, Duration.ofMillis(500));
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);
    }

    @Test
    void compareAsyncReturnsOkQuoteForEachSuccessfulShop() {
        List<ShopPriceFetcher> fetchers = List.of(
                new SimulatedShopPriceFetcher("ShopA", 1000, 10, false),
                new SimulatedShopPriceFetcher("ShopB", 900, 10, false));

        List<PriceQuote> quotes = service.compareAsync("ノートPC", fetchers).join();

        assertEquals(2, quotes.size());
        assertTrue(quotes.stream().allMatch(q -> q.status() == PriceQuote.Status.OK));
    }

    @Test
    void compareAsyncMarksFailedShopAsFailedWithoutAffectingOthers() {
        List<ShopPriceFetcher> fetchers = List.of(
                new SimulatedShopPriceFetcher("ShopA", 1000, 10, false),
                new SimulatedShopPriceFetcher("ShopB", 900, 10, true));

        List<PriceQuote> quotes = service.compareAsync("ノートPC", fetchers).join();

        PriceQuote shopA = quotes.stream().filter(q -> q.shopName().equals("ShopA")).findFirst().orElseThrow();
        PriceQuote shopB = quotes.stream().filter(q -> q.shopName().equals("ShopB")).findFirst().orElseThrow();
        assertEquals(PriceQuote.Status.OK, shopA.status());
        assertEquals(PriceQuote.Status.FAILED, shopB.status());
    }

    @Test
    void compareAsyncMarksSlowShopAsTimeoutWhenExceedingConfiguredTimeout() {
        PriceComparisonService shortTimeoutService = new PriceComparisonService(executor, Duration.ofMillis(50));
        List<ShopPriceFetcher> fetchers = List.of(new SimulatedShopPriceFetcher("ShopSlow", 1000, 300, false));

        List<PriceQuote> quotes = shortTimeoutService.compareAsync("ノートPC", fetchers).join();

        assertEquals(PriceQuote.Status.TIMEOUT, quotes.get(0).status());
    }

    @Test
    void compareAsyncQueriesShopsConcurrentlyRatherThanSequentially() {
        List<ShopPriceFetcher> fetchers = List.of(
                new SimulatedShopPriceFetcher("ShopA", 1000, 200, false),
                new SimulatedShopPriceFetcher("ShopB", 900, 200, false),
                new SimulatedShopPriceFetcher("ShopC", 1100, 200, false));

        long start = System.currentTimeMillis();
        service.compareAsync("ノートPC", fetchers).join();
        long elapsed = System.currentTimeMillis() - start;

        // 逐次実行なら600ms以上かかるはずだが、並行実行のため十分な余裕を見ても500ms未満で終わる。
        assertTrue(elapsed < 500);
    }

    @Test
    void cheapestOfSelectsLowestPriceAmongOkQuotes() {
        List<PriceQuote> quotes = List.of(
                PriceQuote.ok("ShopA", 1000),
                PriceQuote.ok("ShopB", 800),
                PriceQuote.failed("ShopC"));

        var cheapest = service.cheapestOf(quotes);

        assertTrue(cheapest.isPresent());
        assertEquals("ShopB", cheapest.get().shopName());
    }

    @Test
    void findCheapestAsyncFailsWithNoAvailablePriceExceptionWhenAllShopsFail() {
        List<ShopPriceFetcher> fetchers = List.of(new SimulatedShopPriceFetcher("ShopA", 1000, 10, true));

        CompletionException thrown = assertThrows(CompletionException.class,
                () -> service.findCheapestAsync("ノートPC", fetchers).join());

        assertInstanceOf(NoAvailablePriceException.class, thrown.getCause());
    }
}
