package com.javalab.httpclient;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonHttpFetcherTest {

    private HttpServer server;
    private int port;
    private final JsonHttpFetcher fetcher = new JsonHttpFetcher();

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void fetchJsonReturnsBodyFor200Response() throws Exception {
        String responseBody = "{\"id\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\"}";
        server.createContext("/user", exchange -> {
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        String result = fetcher.fetchJson("http://localhost:" + port + "/user");

        assertEquals(responseBody, result);
    }

    @Test
    void fetchJsonThrowsExceptionFor404Response() throws Exception {
        server.createContext("/missing", exchange -> {
            byte[] bytes = "not found".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        assertThrows(HttpClientException.class,
                () -> fetcher.fetchJson("http://localhost:" + port + "/missing"));
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void fetchJsonThrowsExceptionWhenConnectionFails() {
        // ".invalid" はRFC 2606で名前解決が行われないことが保証された予約TLDのため、
        // OSごとのTCP接続拒否タイミングに依存せず確実かつ迅速に接続失敗を再現できる。
        assertThrows(HttpClientException.class,
                () -> fetcher.fetchJson("http://this-host-does-not-exist.invalid/user"));
    }

    @Test
    void fetchUserDeserializesJsonIntoUserRecord() throws Exception {
        String responseBody = "{\"id\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\"}";
        server.createContext("/user", exchange -> {
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        User user = fetcher.fetchUser("http://localhost:" + port + "/user");

        assertEquals(1, user.id());
        assertEquals("Alice", user.name());
        assertEquals("alice@example.com", user.email());
    }

    @Test
    void fetchUserThrowsExceptionForMalformedJson() {
        server.createContext("/broken", exchange -> {
            byte[] bytes = "not valid json".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        assertThrows(HttpClientException.class,
                () -> fetcher.fetchUser("http://localhost:" + port + "/broken"));
    }
}
