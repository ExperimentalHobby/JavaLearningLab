package com.javalab.completablefuturedemo;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 複数店舗への価格問い合わせを並行実行し、結果を集約するサービス。
 * CompletableFutureの非同期チェーン(supplyAsync/orTimeout/exceptionally/allOf/thenApply)を
 * 実践するための題材として設計している。
 */
public class PriceComparisonService {

    private final Executor executor;
    private final Duration timeout;

    public PriceComparisonService(Executor executor, Duration timeout) {
        this.executor = executor;
        this.timeout = timeout;
    }

    /**
     * 全店舗へ並行して価格を問い合わせ、結果を集約する。
     * 個々の店舗の失敗/タイムアウトは全体を失敗させず、対応する状態の{@link PriceQuote}に変換される。
     * @param productName 商品名
     * @param fetchers 問い合わせ対象の店舗一覧
     * @return 全店舗分の見積もり一覧
     */
    public CompletableFuture<List<PriceQuote>> compareAsync(String productName, List<ShopPriceFetcher> fetchers) {
        List<CompletableFuture<PriceQuote>> futures = fetchers.stream()
                .map(fetcher -> fetchQuoteAsync(fetcher, productName))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).toList());
    }

    private CompletableFuture<PriceQuote> fetchQuoteAsync(ShopPriceFetcher fetcher, String productName) {
        return CompletableFuture
                .supplyAsync(() -> fetchQuote(fetcher, productName), executor)
                .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .exceptionally(ex -> classifyFailure(fetcher.shopName(), ex));
    }

    private PriceQuote fetchQuote(ShopPriceFetcher fetcher, String productName) {
        try {
            return PriceQuote.ok(fetcher.shopName(), fetcher.fetchPrice(productName));
        } catch (Exception e) {
            throw new CompletionException(e);
        }
    }

    private PriceQuote classifyFailure(String shopName, Throwable ex) {
        if (ex instanceof TimeoutException || ex.getCause() instanceof TimeoutException) {
            return PriceQuote.timeout(shopName);
        }
        return PriceQuote.failed(shopName);
    }

    /**
     * 見積もり一覧の中から、正常取得できたものに限定して最安値を選ぶ。
     * @param quotes 見積もり一覧
     * @return 最安値の見積もり。正常取得分が1件も無い場合は空
     */
    public Optional<PriceQuote> cheapestOf(List<PriceQuote> quotes) {
        return quotes.stream()
                .filter(q -> q.status() == PriceQuote.Status.OK)
                .min(Comparator.comparingInt(PriceQuote::price));
    }

    /**
     * 全店舗へ問い合わせ、最安値の見積もりを返す。
     * @param productName 商品名
     * @param fetchers 問い合わせ対象の店舗一覧
     * @return 最安値の見積もり
     * @throws NoAvailablePriceException 有効な見積もりが1件も得られなかった場合
     */
    public CompletableFuture<PriceQuote> findCheapestAsync(String productName, List<ShopPriceFetcher> fetchers) {
        return compareAsync(productName, fetchers)
                .thenApply(quotes -> cheapestOf(quotes)
                        .orElseThrow(() -> new NoAvailablePriceException(productName)));
    }
}
