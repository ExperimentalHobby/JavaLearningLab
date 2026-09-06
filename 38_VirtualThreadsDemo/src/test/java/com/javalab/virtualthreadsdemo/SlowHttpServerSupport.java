package com.javalab.virtualthreadsdemo;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * テスト用に、指定件数・指定遅延の「疑似的に遅いエンドポイント」を持つHTTPサーバーを起動するヘルパー。
 * サーバー側もマルチスレッドで捌けるよう{@code setExecutor}するのがポイント
 * (これをしないとHttpServerはリクエストを1件ずつ直列処理してしまい、クライアント側の並行性を
 * 正しく計測できない)。
 */
final class SlowHttpServerSupport implements AutoCloseable {

    private final HttpServer server;
    private final List<String> urls = new ArrayList<>();

    private SlowHttpServerSupport(HttpServer server) {
        this.server = server;
    }

    static SlowHttpServerSupport start(int endpointCount, int delayMillis) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        SlowHttpServerSupport support = new SlowHttpServerSupport(server);
        for (int i = 0; i < endpointCount; i++) {
            String path = "/endpoint" + i;
            server.createContext(path, exchange -> {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                byte[] body = "OK".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(body);
                }
            });
            support.urls.add("http://localhost:" + server.getAddress().getPort() + path);
        }

        server.start();
        return support;
    }

    List<String> urls() {
        return urls;
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
