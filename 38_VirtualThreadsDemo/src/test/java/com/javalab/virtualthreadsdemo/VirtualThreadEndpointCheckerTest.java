package com.javalab.virtualthreadsdemo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void checkAll_oneUnreachableUrl_stillReturnsResultsForOthers() throws Exception {
        // 修正前は1件の疎通失敗(IOException)がExecutionException→IllegalStateExceptionとなって
        // checkAll全体から伝播し、他の正常なURLの結果も失われていた。
        try (SlowHttpServerSupport server = SlowHttpServerSupport.start(1, 10)) {
            VirtualThreadEndpointChecker checker = new VirtualThreadEndpointChecker();
            List<String> urls = new ArrayList<>(server.urls());
            urls.add("http://localhost:1/unreachable");

            List<CheckResult> results = checker.checkAll(urls);

            assertEquals(2, results.size());
            assertTrue(results.stream().anyMatch(CheckResult::success));
            assertTrue(results.stream().anyMatch(r -> !r.success()));
        }
    }

    @Test
    void checkAll_invalidUrlSyntax_returnsFailureResultInsteadOfThrowing() {
        // 修正前はURI.create(url)のIllegalArgumentExceptionがExecutionException経由で
        // IllegalStateExceptionに包まれ、Main.runのcatch (IllegalArgumentException)をすり抜けていた。
        VirtualThreadEndpointChecker checker = new VirtualThreadEndpointChecker();

        List<CheckResult> results = checker.checkAll(List.of("not a url"));

        assertEquals(1, results.size());
        assertFalse(results.get(0).success());
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
