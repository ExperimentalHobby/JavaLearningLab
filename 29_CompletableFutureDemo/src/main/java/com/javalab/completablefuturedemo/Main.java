package com.javalab.completablefuturedemo;

import java.io.PrintStream;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 並行処理デモのエントリーポイント。
 * {@code compare}コマンドで複数店舗への価格問い合わせを並行実行し、
 * CompletableFutureの非同期チェーンによる結果集約を体験できる。
 */
public class Main {

    private static final Duration TIMEOUT = Duration.ofMillis(400);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            List<ShopPriceFetcher> fetchers = List.of(
                    new SimulatedShopPriceFetcher("ShopA", 1000, 300, false),
                    new SimulatedShopPriceFetcher("ShopB", 950, 500, false),
                    new SimulatedShopPriceFetcher("ShopC", 1200, 100, true));
            PriceComparisonService service = new PriceComparisonService(executor, TIMEOUT);
            run(new Scanner(System.in), System.out, service, fetchers);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}/依存コンポーネントを
     * 差し替えられるよう分離している。
     * コマンド: {@code compare <商品名>} / {@code exit}。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     * @param service 価格比較を担うサービス
     * @param fetchers 問い合わせ対象の店舗一覧
     */
    static void run(Scanner scanner, PrintStream out, PriceComparisonService service, List<ShopPriceFetcher> fetchers) {
        out.println("並行処理デモ(CompletableFuture)。コマンド: compare <商品名> / exit");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+", 2);
            String command = parts[0];
            try {
                switch (command) {
                    case "exit" -> {
                        return;
                    }
                    case "compare" -> handleCompare(service, fetchers, parts, out);
                    default -> out.println("不明なコマンドです: " + line);
                }
            } catch (RuntimeException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleCompare(PriceComparisonService service, List<ShopPriceFetcher> fetchers,
                                       String[] parts, PrintStream out) {
        String productName = parts[1];
        List<PriceQuote> quotes = service.compareAsync(productName, fetchers).join();
        for (PriceQuote quote : quotes) {
            out.println(quote.shopName() + ": " + describe(quote));
        }
        Optional<PriceQuote> cheapest = service.cheapestOf(quotes);
        if (cheapest.isPresent()) {
            PriceQuote quote = cheapest.get();
            out.println("最安値: " + quote.shopName() + " " + quote.price() + "円");
        } else {
            out.println("入手可能な価格がありませんでした");
        }
    }

    private static String describe(PriceQuote quote) {
        return switch (quote.status()) {
            case OK -> quote.price() + "円";
            case FAILED -> "取得失敗";
            case TIMEOUT -> "タイムアウト";
        };
    }
}
