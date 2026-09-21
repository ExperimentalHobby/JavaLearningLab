package com.javalab.completablefuturedemo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
    void timeoutInterruptsUnderlyingTaskSoThreadPoolIsFreedPromptly() throws Exception {
        // orTimeout()はFutureをタイムアウト例外で完了させるだけで、supplyAsyncに投入済みの
        // タスク本体(Thread.sleep中)は動き続けてしまっていた問題への対応。
        // 修正前は、1スレッドしかないプールでタイムアウト発生後もそのスレッドが
        // ずっとThread.sleep(2000ms)で占有され続けるため、別のタスクをすぐには実行できなかった。
        // 修正後は実際にスレッドへ割り込むため、タイムアウト後すぐにプールが解放されるはずである。
        ExecutorService singleThreadPool = Executors.newFixedThreadPool(1);
        try {
            PriceComparisonService shortTimeoutService =
                    new PriceComparisonService(singleThreadPool, Duration.ofMillis(50));
            List<ShopPriceFetcher> slowFetcher =
                    List.of(new SimulatedShopPriceFetcher("ShopSlow", 1000, 2000, false));

            long start = System.currentTimeMillis();
            shortTimeoutService.compareAsync("ノートPC", slowFetcher).join();

            // プールが解放されていれば、この軽量タスクはすぐに実行できるはず。
            CompletableFuture<String> probe = CompletableFuture.supplyAsync(() -> "ok", singleThreadPool);
            String result = probe.get(500, TimeUnit.MILLISECONDS);
            long elapsed = System.currentTimeMillis() - start;

            assertEquals("ok", result);
            assertTrue(elapsed < 1000, "elapsed=" + elapsed + "ms (修正前は2000ms近くかかっていたはず)");
        } finally {
            singleThreadPool.shutdownNow();
        }
    }

    @Test
    void findCombinedCheapestTotalAsyncCombinesTwoIndependentCheapestSearchesViaThenCombine() {
        // thenCombine: 2つの独立した非同期結果(2商品それぞれの最安値検索)を1つに合成する題材。
        List<ShopPriceFetcher> fetchers = List.of(
                new SimulatedShopPriceFetcher("ShopA", 1000, 10, false),
                new SimulatedShopPriceFetcher("ShopB", 900, 10, false));

        int total = service.findCombinedCheapestTotalAsync("ノートPC", "マウス", fetchers).join();

        // 商品名によらずShopBが常に最安値(900円)を返すシミュレータ構成のため、
        // ノートPC(900) + マウス(900) = 1800円になるはず。
        assertEquals(1800, total);
    }

    @Test
    void findCheapestAndConfirmAsyncReconfirmsPriceWithSameShopViaThenCompose() {
        // thenCompose: 最安値を求めた「後」で、その店舗へ再確認問い合わせをする非同期処理の連鎖。
        // (2つ目の非同期処理が1つ目の結果に依存するため、thenCombineでは表現できない)。
        List<ShopPriceFetcher> fetchers = List.of(
                new SimulatedShopPriceFetcher("ShopA", 1000, 10, false),
                new SimulatedShopPriceFetcher("ShopB", 900, 10, false));

        PriceQuote confirmed = service.findCheapestAndConfirmAsync("ノートPC", fetchers).join();

        assertEquals("ShopB", confirmed.shopName());
        assertEquals(PriceQuote.Status.OK, confirmed.status());
        assertEquals(900, confirmed.price());
    }

    @Test
    void describeCheapestAsyncDescribesSuccessCaseViaHandle() {
        // handle: 成功(quote)/失敗(ex)のどちらか一方が必ずnullで渡され、
        // 両方のケースを1箇所で処理できる(exceptionallyは失敗時のみ、thenApplyは成功時のみ)。
        List<ShopPriceFetcher> fetchers = List.of(new SimulatedShopPriceFetcher("ShopA", 1000, 10, false));

        String description = service.describeCheapestAsync("ノートPC", fetchers).join();

        assertEquals("ShopAが最安値: 1000円", description);
    }

    @Test
    void describeCheapestAsyncDescribesFailureCaseViaHandle() {
        List<ShopPriceFetcher> fetchers = List.of(new SimulatedShopPriceFetcher("ShopA", 1000, 10, true));

        String description = service.describeCheapestAsync("ノートPC", fetchers).join();

        assertEquals("取得できませんでした: ノートPC", description);
    }

    @Test
    void findCheapestAsyncFailsWithNoAvailablePriceExceptionWhenAllShopsFail() {
        List<ShopPriceFetcher> fetchers = List.of(new SimulatedShopPriceFetcher("ShopA", 1000, 10, true));

        CompletionException thrown = assertThrows(CompletionException.class,
                () -> service.findCheapestAsync("ノートPC", fetchers).join());

        assertInstanceOf(NoAvailablePriceException.class, thrown.getCause());
    }
}
