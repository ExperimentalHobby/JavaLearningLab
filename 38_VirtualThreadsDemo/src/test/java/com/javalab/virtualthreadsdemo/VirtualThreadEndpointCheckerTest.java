package com.javalab.virtualthreadsdemo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VirtualThreadEndpointCheckerTest {

    @Test
    void checkAll_returnsStatusCodeForEachUrl() throws Exception {
        try (SlowHttpServerSupport server = SlowHttpServerSupport.start(3, 50)) {
            VirtualThreadEndpointChecker checker = new VirtualThreadEndpointChecker();

            List<CheckResult> results = checker.checkAll(server.urls());

            assertEquals(3, results.size());
            assertTrue(results.stream().allMatch(r -> r.statusCode() == 200));
        }
    }

    @Test
    void checkAll_checksFiveSlowEndpointsConcurrently() throws Exception {
        int endpointCount = 5;
        int delayMillis = 200;

        try (SlowHttpServerSupport server = SlowHttpServerSupport.start(endpointCount, delayMillis)) {
            VirtualThreadEndpointChecker checker = new VirtualThreadEndpointChecker();

            long start = System.nanoTime();
            List<CheckResult> results = checker.checkAll(server.urls());
            long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

            assertEquals(endpointCount, results.size());
            // 逐次実行なら約1000ms(200ms×5)かかるはずだが、並行実行であれば700ms未満で終わることを確認する。
            assertTrue(elapsedMillis < 700, "elapsed=" + elapsedMillis + "ms");
        }
    }
}
