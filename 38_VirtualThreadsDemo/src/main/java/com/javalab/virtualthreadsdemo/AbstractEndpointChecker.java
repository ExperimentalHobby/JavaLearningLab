package com.javalab.virtualthreadsdemo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * URLごとにタスクを{@link ExecutorService}へ投入して並行アクセスする共通処理をまとめた基底クラス。
 * サブクラスは{@link #createExecutor()}でスレッドモデル(Virtual Thread/Platform Thread)だけを差し替える。
 */
abstract class AbstractEndpointChecker implements EndpointChecker {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    /** このチェッカーが使う{@link ExecutorService}を生成する。呼び出しごとに新規生成し、{@code checkAll}内でクローズされる。 */
    protected abstract ExecutorService createExecutor();

    @Override
    public List<CheckResult> checkAll(List<String> urls) {
        try (ExecutorService executor = createExecutor()) {
            List<Future<CheckResult>> futures = urls.stream()
                    .map(url -> executor.submit(() -> check(url)))
                    .toList();

            return futures.stream().map(this::join).toList();
        }
    }

    // 例外を伝播させず、失敗もCheckResultとして返す。1件のURL不到達・URL形式不正が
    // checkAll全体(=他のURLの結果)を道連れにしないようにするため。
    private CheckResult check(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().timeout(TIMEOUT).build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return CheckResult.success(url, response.statusCode());
        } catch (IllegalArgumentException e) {
            return CheckResult.failure(url, "不正なURLです: " + url);
        } catch (IOException e) {
            return CheckResult.failure(url, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CheckResult.failure(url, e.getMessage());
        }
    }

    private CheckResult join(Future<CheckResult> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        }
    }
}
