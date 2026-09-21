package com.javalab.designpatterns.singleton;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link AppLogger} のSingletonパターン実装を検証するテスト。
 * 「常に同一インスタンスが返る」ことと「そのインスタンスが状態(ログ)を共有する」ことの
 * 両方を確認する(前者だけでは、状態を持たないただの共有関数と区別がつかないため)。
 */
class AppLoggerTest {

    @Test
    void getInstanceAlwaysReturnsSameInstanceAndSharesState() {
        // firstでログを記録した内容が、別変数で取得したsecond経由でも参照できることを確認する
        // (firstとsecondが同一インスタンスであることの実質的な証明)。
        AppLogger first = AppLogger.getInstance();
        AppLogger second = AppLogger.getInstance();

        first.log("最初のログ");

        assertSame(first, second);
        assertTrue(second.logs().contains("最初のログ"));
    }

    @Test
    void logIsThreadSafeUnderConcurrentWrites() throws InterruptedException {
        // JavaDocに「アプリ全体で共有するログ蓄積先」と書かれているが、内部実装がArrayList(非スレッドセーフ)
        // だと並行書き込みで要素が失われたり例外になったりする。多数のスレッドから同時にlog()を呼んでも
        // 記録件数が失われないことを確認する。
        int threadCount = 20;
        int perThread = 50;
        String prefix = "concurrency-" + System.nanoTime() + "-";
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int t = 0; t < threadCount; t++) {
            int threadId = t;
            executor.submit(() -> {
                for (int i = 0; i < perThread; i++) {
                    AppLogger.getInstance().log(prefix + threadId + "-" + i);
                }
                latch.countDown();
            });
        }
        latch.await();
        executor.shutdown();

        long count = AppLogger.getInstance().logs().stream()
                .filter(s -> s.startsWith(prefix))
                .count();
        assertEquals(threadCount * perThread, count);
    }
}
