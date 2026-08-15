package com.javalab.completablefuturedemo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

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
    void runShowsQuoteListAndCheapestForCompareCommand() {
        List<ShopPriceFetcher> fetchers = List.of(
                new SimulatedShopPriceFetcher("ShopA", 1000, 10, false),
                new SimulatedShopPriceFetcher("ShopB", 900, 10, false));
        Scanner scanner = new Scanner("compare ノートPC\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, service, fetchers);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("ShopA"));
        assertTrue(result.contains("ShopB"));
        assertTrue(result.contains("最安値"));
        assertTrue(result.contains("ShopB"));
    }

    @Test
    void runShowsNoAvailablePriceMessageWhenAllShopsFail() {
        List<ShopPriceFetcher> fetchers = List.of(new SimulatedShopPriceFetcher("ShopA", 1000, 10, true));
        Scanner scanner = new Scanner("compare ノートPC\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, service, fetchers);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("取得失敗"));
        assertTrue(result.contains("入手可能な価格がありませんでした"));
    }
}
