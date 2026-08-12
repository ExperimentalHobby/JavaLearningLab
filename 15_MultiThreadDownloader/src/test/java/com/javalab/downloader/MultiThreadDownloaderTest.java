package com.javalab.downloader;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiThreadDownloaderTest {

    private HttpServer server;
    private int port;
    private ExecutorService serverExecutor;
    private final MultiThreadDownloader downloader = new MultiThreadDownloader();

    @TempDir
    Path tempDir;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        // デフォルトのHttpServerはリクエストを単一スレッドで直列処理するため、
        // クライアント側の並行ダウンロードを正しく検証できるよう複数スレッドのexecutorを設定する。
        serverExecutor = Executors.newFixedThreadPool(4);
        server.setExecutor(serverExecutor);
        port = server.getAddress().getPort();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
        serverExecutor.shutdown();
    }

    @Test
    void downloadAllSavesSingleUrlToFile() throws Exception {
        String content = "hello world";
        server.createContext("/file", exchange -> respond(exchange, content));
        server.start();

        Path destination = tempDir.resolve("file.txt");
        DownloadRequest request = new DownloadRequest(URI.create("http://localhost:" + port + "/file"), destination);

        List<DownloadResult> results = downloader.downloadAll(List.of(request), 1);

        assertEquals(1, results.size());
        assertTrue(results.get(0).success());
        assertEquals(content, Files.readString(destination, StandardCharsets.UTF_8));
    }

    @Test
    void downloadAllDownloadsMultipleUrlsSuccessfully() throws Exception {
        server.createContext("/a", exchange -> respond(exchange, "content-a"));
        server.createContext("/b", exchange -> respond(exchange, "content-b"));
        server.createContext("/c", exchange -> respond(exchange, "content-c"));
        server.start();

        Path destA = tempDir.resolve("a.txt");
        Path destB = tempDir.resolve("b.txt");
        Path destC = tempDir.resolve("c.txt");
        List<DownloadRequest> requests = List.of(
                new DownloadRequest(URI.create("http://localhost:" + port + "/a"), destA),
                new DownloadRequest(URI.create("http://localhost:" + port + "/b"), destB),
                new DownloadRequest(URI.create("http://localhost:" + port + "/c"), destC));

        List<DownloadResult> results = downloader.downloadAll(requests, 3);

        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(DownloadResult::success));
        assertEquals("content-a", Files.readString(destA, StandardCharsets.UTF_8));
        assertEquals("content-b", Files.readString(destB, StandardCharsets.UTF_8));
        assertEquals("content-c", Files.readString(destC, StandardCharsets.UTF_8));
    }

    @Test
    void downloadAllRecordsFailureForOneUrlWithoutAffectingOthers() throws Exception {
        server.createContext("/ok", exchange -> respond(exchange, "content-ok"));
        server.createContext("/missing", exchange -> {
            byte[] bytes = "not found".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        Path destOk = tempDir.resolve("ok.txt");
        Path destMissing = tempDir.resolve("missing.txt");
        List<DownloadRequest> requests = List.of(
                new DownloadRequest(URI.create("http://localhost:" + port + "/ok"), destOk),
                new DownloadRequest(URI.create("http://localhost:" + port + "/missing"), destMissing));

        List<DownloadResult> results = downloader.downloadAll(requests, 2);

        assertEquals(2, results.size());
        assertTrue(results.get(0).success());
        assertTrue(results.get(1).errorMessage().contains("404"));
        assertEquals("content-ok", Files.readString(destOk, StandardCharsets.UTF_8));
    }

    @Test
    void downloadAllRunsDownloadsConcurrently() throws Exception {
        // 各エンドポイントに300msの遅延を入れる。3件を逐次実行すれば900ms以上かかるが、
        // 3スレッドで並行実行すれば概ね300ms強で完了するはずであり、これで並行動作を検証する。
        int delayMillis = 300;
        for (String path : List.of("/x", "/y", "/z")) {
            server.createContext(path, exchange -> {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                respond(exchange, "content");
            });
        }
        server.start();

        List<DownloadRequest> requests = List.of(
                new DownloadRequest(URI.create("http://localhost:" + port + "/x"), tempDir.resolve("x.txt")),
                new DownloadRequest(URI.create("http://localhost:" + port + "/y"), tempDir.resolve("y.txt")),
                new DownloadRequest(URI.create("http://localhost:" + port + "/z"), tempDir.resolve("z.txt")));

        long start = System.currentTimeMillis();
        List<DownloadResult> results = downloader.downloadAll(requests, 3);
        long elapsed = System.currentTimeMillis() - start;

        assertTrue(results.stream().allMatch(DownloadResult::success));
        assertTrue(elapsed < delayMillis * 3,
                "並行実行なら " + (delayMillis * 3) + "ms未満で完了するはずだが " + elapsed + "ms かかった");
    }

    private static void respond(com.sun.net.httpserver.HttpExchange exchange, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
