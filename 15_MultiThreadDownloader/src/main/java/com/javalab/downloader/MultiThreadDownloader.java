package com.javalab.downloader;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * 複数URLを{@link ExecutorService}で並行ダウンロードし、それぞれ指定ファイルへ保存するクラス。
 */
public class MultiThreadDownloader {

    // URL数がそのままスレッド数になってしまうと、大量のURLを渡された際にスレッドが
    // 際限なく生成されてしまうため、上限を設ける。
    private static final int MAX_THREAD_COUNT = 8;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    /**
     * 指定されたダウンロード指示を並行実行する。
     * 個々のダウンロードの失敗(通信エラー・書き込みエラー・異常ステータス)は例外を投げず
     * {@link DownloadResult#success()}が{@code false}の結果として記録し、他のダウンロードには影響させない。
     * @param requests ダウンロード指示の一覧
     * @param threadCount 使用するスレッド数の希望値({@link #MAX_THREAD_COUNT}を超える場合はクランプされる)
     * @return {@code requests}と同じ順序の結果一覧
     */
    public List<DownloadResult> downloadAll(List<DownloadRequest> requests, int threadCount) {
        return downloadAll(requests, threadCount, result -> { });
    }

    /**
     * {@link #downloadAll(List, int)}と同様だが、1件のダウンロードが完了するたびに
     * {@code progressListener}へその結果を通知する(完了順に呼ばれるため、戻り値の順序とは異なりうる)。
     * 進捗(件数・バイト数・完了率)を表示したい呼び出し側向け。
     * @param requests ダウンロード指示の一覧
     * @param threadCount 使用するスレッド数の希望値
     * @param progressListener 1件完了するたびに呼ばれるコールバック
     * @return {@code requests}と同じ順序の結果一覧
     */
    public List<DownloadResult> downloadAll(
            List<DownloadRequest> requests, int threadCount, Consumer<DownloadResult> progressListener) {
        int effectiveThreadCount = Math.max(1, Math.min(threadCount, MAX_THREAD_COUNT));
        ExecutorService executor = Executors.newFixedThreadPool(effectiveThreadCount);
        try {
            List<Future<DownloadResult>> futures = requests.stream()
                    .map(request -> executor.submit(() -> {
                        DownloadResult result = downloadOne(request);
                        progressListener.accept(result);
                        return result;
                    }))
                    .toList();
            List<DownloadResult> results = new ArrayList<>();
            for (int i = 0; i < futures.size(); i++) {
                results.add(joinResult(futures.get(i), requests.get(i)));
            }
            return results;
        } finally {
            executor.shutdown();
        }
    }

    private DownloadResult joinResult(Future<DownloadResult> future, DownloadRequest request) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            // 割り込みを飲み込まず、呼び出し元が中断を検知できるよう割り込み状態を復元する。
            Thread.currentThread().interrupt();
            return new DownloadResult(request, false, 0, "ダウンロード処理の待機中に中断されました");
        } catch (ExecutionException e) {
            // downloadOne自体は例外を投げず失敗結果を返す設計のため、ここに到達するのは
            // 想定外の実行時エラーのみ。それでもアプリ全体を落とさず失敗結果として扱う。
            return new DownloadResult(request, false, 0, "ダウンロード処理でエラーが発生しました: " + e.getCause());
        }
    }

    private DownloadResult downloadOne(DownloadRequest request) {
        HttpRequest httpRequest;
        try {
            httpRequest = HttpRequest.newBuilder(request.url()).timeout(REQUEST_TIMEOUT).GET().build();
        } catch (IllegalArgumentException e) {
            // 未対応スキーム(例: ftp://)等はHttpRequest.newBuilder()がIllegalArgumentExceptionを
            // 投げる。他の失敗と同じくDownloadResultの失敗として扱い、Future越しに例外を伝播させない。
            return new DownloadResult(request, false, 0, "不正なURLです: " + e.getMessage());
        }

        // 保存先ディレクトリの有無を事前に検証する。ofFile()がスローする例外は環境によって
        // FileSystemExceptionではなく汎用のIOExceptionにラップされることがあり、型による
        // 通信エラーとの判別が信頼できないため、ここで先に判定してから通信を開始する。
        Path parentDir = request.destination().toAbsolutePath().getParent();
        if (parentDir != null && !Files.isDirectory(parentDir)) {
            return new DownloadResult(
                    request, false, 0, "ファイルの書き込みに失敗しました: 保存先ディレクトリが存在しません: " + parentDir);
        }

        HttpResponse<Path> response;
        try {
            // レスポンス全体をメモリに載せるofByteArray()ではなく、ストリームで直接ファイルへ
            // 保存するofFile()を使うことで、大きいファイルでもOutOfMemoryErrorにならないようにする。
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofFile(request.destination()));
        } catch (FileSystemException e) {
            // 上記の事前チェックをすり抜けた書き込みエラー(権限不足等)も、環境によっては
            // FileSystemExceptionとしてスローされるため、通信エラーと区別する。
            return new DownloadResult(request, false, 0, "ファイルの書き込みに失敗しました: " + e.getMessage());
        } catch (IOException e) {
            return new DownloadResult(request, false, 0, "HTTP通信に失敗しました: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new DownloadResult(request, false, 0, "HTTP通信が中断されました");
        }

        if (response.statusCode() != 200) {
            deleteQuietly(response.body());
            return new DownloadResult(request, false, 0, "HTTPステータスが異常です: " + response.statusCode());
        }
        return new DownloadResult(request, true, fileSizeQuietly(response.body()), null);
    }

    private static void deleteQuietly(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
            // 異常ステータス時の後始末に失敗しても、呼び出し元へは異常ステータスの結果を優先して返す。
        }
    }

    private static long fileSizeQuietly(Path file) {
        try {
            return Files.size(file);
        } catch (IOException e) {
            return 0;
        }
    }
}
