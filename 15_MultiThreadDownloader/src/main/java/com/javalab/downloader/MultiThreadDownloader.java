package com.javalab.downloader;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 複数URLを{@link ExecutorService}で並行ダウンロードし、それぞれ指定ファイルへ保存するクラス。
 */
public class MultiThreadDownloader {

    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * 指定されたダウンロード指示を{@code threadCount}個のスレッドで並行実行する。
     * 個々のダウンロードの失敗(通信エラー・異常ステータス)は例外を投げず
     * {@link DownloadResult#success()}が{@code false}の結果として記録し、他のダウンロードには影響させない。
     * @param requests ダウンロード指示の一覧
     * @param threadCount 使用するスレッド数
     * @return {@code requests}と同じ順序の結果一覧
     */
    public List<DownloadResult> downloadAll(List<DownloadRequest> requests, int threadCount) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try {
            List<Future<DownloadResult>> futures = requests.stream()
                    .map(request -> executor.submit(() -> downloadOne(request)))
                    .toList();
            return futures.stream()
                    .map(this::joinResult)
                    .toList();
        } finally {
            executor.shutdown();
        }
    }

    private DownloadResult joinResult(Future<DownloadResult> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new IllegalStateException("ダウンロード処理の待機中にエラーが発生しました", e);
        }
    }

    private DownloadResult downloadOne(DownloadRequest request) {
        HttpRequest httpRequest = HttpRequest.newBuilder(request.url()).GET().build();
        try {
            HttpResponse<byte[]> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                return new DownloadResult(request, false, 0, "HTTPステータスが異常です: " + response.statusCode());
            }
            Files.write(request.destination(), response.body());
            return new DownloadResult(request, true, response.body().length, null);
        } catch (IOException e) {
            return new DownloadResult(request, false, 0, "HTTP通信に失敗しました: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new DownloadResult(request, false, 0, "HTTP通信が中断されました");
        }
    }
}
