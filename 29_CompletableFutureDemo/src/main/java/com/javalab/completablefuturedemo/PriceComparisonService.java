package com.javalab.completablefuturedemo;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 複数店舗への価格問い合わせを並行実行し、結果を集約するサービス。
 * CompletableFutureの非同期チェーン(supplyAsync/orTimeout/exceptionally/allOf/thenApply/
 * thenCombine/thenCompose/handle/whenComplete)を実践するための題材として設計している。
 */
public class PriceComparisonService {

    private final ExecutorService executor;
    private final Duration timeout;

    public PriceComparisonService(ExecutorService executor, Duration timeout) {
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
        // orTimeout()はCompletableFutureをタイムアウト例外で完了させるだけで、
        // supplyAsyncに投入済みのタスク本体(この場合fetchQuoteの中のThread.sleep)は
        // 止まらずに動き続けてしまう。CompletableFuture#cancel()もmayInterruptIfRunningを
        // 無視する仕様(JavaDoc参照)のため、実際にタスクを中断させることはできない。
        // 代わりにExecutorService#submit()で得られる本物のFuture(FutureTask)を使う。
        // FutureTask#cancel(true)は本物の割り込み(Thread#interrupt())をサポートしているため、
        // タイムアウト時にこちらを呼ぶことで、ワーカースレッドを実際に解放できる。
        CompletableFuture<PriceQuote> resultFuture = new CompletableFuture<>();
        Future<?> taskHandle = executor.submit(() -> {
            try {
                resultFuture.complete(fetchQuote(fetcher, productName));
            } catch (Throwable t) {
                resultFuture.completeExceptionally(t);
            }
        });

        return resultFuture
                .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                // whenCompleteは結果を書き換えず「完了したこと」を横から観察するためのAPI。
                // タイムアウトで完了したと分かったら、実際のタスクへ割り込みを送る。
                .whenComplete((quote, ex) -> {
                    if (ex instanceof TimeoutException) {
                        taskHandle.cancel(true);
                    }
                })
                .exceptionally(ex -> classifyFailure(fetcher.shopName(), ex));
    }

    private PriceQuote fetchQuote(ShopPriceFetcher fetcher, String productName) {
        try {
            return PriceQuote.ok(fetcher.shopName(), fetcher.fetchPrice(productName));
        } catch (ShopFetchException e) {
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

    /**
     * 2商品それぞれの最安値を並行して求め、両方の検索が完了したら合計金額を算出する。
     * thenCombineは2つの独立した非同期結果を1つに合成する用途に使う
     * (thenComposeと異なり、2つ目の処理は1つ目の結果に依存せず並行して開始できる)。
     * @param productNameA 商品Aの名前
     * @param productNameB 商品Bの名前
     * @param fetchers 問い合わせ対象の店舗一覧
     * @return 2商品の最安値の合計金額
     */
    public CompletableFuture<Integer> findCombinedCheapestTotalAsync(
            String productNameA, String productNameB, List<ShopPriceFetcher> fetchers) {
        CompletableFuture<PriceQuote> cheapestA = findCheapestAsync(productNameA, fetchers);
        CompletableFuture<PriceQuote> cheapestB = findCheapestAsync(productNameB, fetchers);
        return cheapestA.thenCombine(cheapestB, (a, b) -> a.price() + b.price());
    }

    /**
     * 最安値の見積もりを求めた後、念のため同じ店舗へ価格を再確認問い合わせする。
     * thenComposeは非同期処理の連鎖に使う(2つ目の問い合わせ先はfindCheapestAsyncの結果=
     * どの店舗が最安値だったかが分かるまで決まらないため、事前に並行実行できずthenCombineでは
     * 表現できない)。
     * @param productName 商品名
     * @param fetchers 問い合わせ対象の店舗一覧
     * @return 最安値だった店舗への再確認結果
     */
    public CompletableFuture<PriceQuote> findCheapestAndConfirmAsync(String productName, List<ShopPriceFetcher> fetchers) {
        return findCheapestAsync(productName, fetchers)
                .thenCompose(quote -> {
                    ShopPriceFetcher matchedFetcher = fetchers.stream()
                            .filter(f -> f.shopName().equals(quote.shopName()))
                            .findFirst()
                            .orElseThrow();
                    return fetchQuoteAsync(matchedFetcher, productName);
                });
    }

    /**
     * 最安値検索の結果を、成功/失敗を問わず1つの文字列に変換する。
     * handleは(正常値, 例外)のどちらか一方が必ずnullで渡され、両方のケースを1箇所で処理できる
     * (exceptionallyは失敗時のみ、thenApplyは成功時のみしか扱えない点と対比できる)。
     * @param productName 商品名
     * @param fetchers 問い合わせ対象の店舗一覧
     * @return 結果を説明する文字列
     */
    public CompletableFuture<String> describeCheapestAsync(String productName, List<ShopPriceFetcher> fetchers) {
        return findCheapestAsync(productName, fetchers)
                .handle((quote, ex) -> ex != null
                        ? "取得できませんでした: " + productName
                        : quote.shopName() + "が最安値: " + quote.price() + "円");
    }
}
