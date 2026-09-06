package com.javalab.virtualthreadsdemo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlatformThreadEndpointCheckerTest {

    @Test
    void checkAll_withEnoughPoolSize_checksConcurrently() throws Exception {
        int endpointCount = 5;
        int delayMillis = 200;

        try (SlowHttpServerSupport server = SlowHttpServerSupport.start(endpointCount, delayMillis)) {
            PlatformThreadEndpointChecker checker = new PlatformThreadEndpointChecker(endpointCount);

            long start = System.nanoTime();
            List<CheckResult> results = checker.checkAll(server.urls());
            long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

            assertEquals(endpointCount, results.size());
            assertTrue(results.stream().allMatch(r -> r.statusCode() == 200));
            // プールサイズ=エンドポイント数なら、Virtual Thread版と同様に並行実行できるはず。
            assertTrue(elapsedMillis < 700, "elapsed=" + elapsedMillis + "ms");
        }
    }

    @Test
    void checkAll_withSmallPoolSize_isThrottledByPoolSize() throws Exception {
        int endpointCount = 6;
        int delayMillis = 150;
        int poolSize = 2;

        try (SlowHttpServerSupport server = SlowHttpServerSupport.start(endpointCount, delayMillis)) {
            PlatformThreadEndpointChecker checker = new PlatformThreadEndpointChecker(poolSize);

            long start = System.nanoTime();
            List<CheckResult> results = checker.checkAll(server.urls());
            long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

            assertEquals(endpointCount, results.size());
            // プールサイズ2・6エンドポイントなら3巡必要になり、合計は3×150ms=450ms以上かかるはず
            // (Virtual Thread版なら5エンドポイント×200msでも700ms未満で終わるのと対照的)。
            assertTrue(elapsedMillis >= 400, "elapsed=" + elapsedMillis + "ms");
        }
    }
}
