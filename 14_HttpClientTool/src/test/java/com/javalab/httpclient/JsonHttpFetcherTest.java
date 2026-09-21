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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link JsonHttpFetcher} のHTTP通信・JSON解析を検証するテスト。
 * モックを使わず、JDK標準の{@link HttpServer}をポート0(空きポート自動割当)で
 * テストごとに実際に起動し、本物のHTTP通信を通して検証する。
 */
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
        // 200以外のステータスコードはfetchJson()内で異常とみなされ、
        // レスポンスボディの中身に関わらずHttpClientExceptionになることを確認する。
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
    void fetchJsonThrowsHttpClientExceptionForInvalidUrlSyntax() {
        // "fetch not a url"のような不正文字を含むURLはURI.create()がIllegalArgumentExceptionを
        // 投げるが、Main.runはHttpClientExceptionしか捕捉していないためREPLごと終了していた。
        assertThrows(HttpClientException.class, () -> fetcher.fetchJson("not a url"));
    }

    @Test
    void fetchJsonThrowsHttpClientExceptionForUnsupportedScheme() {
        // "ftp://example.com"のような未対応スキームはHttpRequest.newBuilder()が
        // IllegalArgumentExceptionを投げる。同様にHttpClientExceptionへ変換する。
        assertThrows(HttpClientException.class, () -> fetcher.fetchJson("ftp://example.com"));
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
    void fetchUserIgnoresUnknownJsonProperties() throws Exception {
        // Jacksonの既定はFAIL_ON_UNKNOWN_PROPERTIES=trueのため、Userに無いフィールド
        // (実在のAPIはほぼ該当する)を含むJSONを解析するとエラーになっていた。
        String responseBody = "{\"id\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\",\"extra\":\"unused\"}";
        server.createContext("/user", exchange -> {
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        User user = fetcher.fetchUser("http://localhost:" + port + "/user");

        assertEquals("Alice", user.name());
    }

    @Test
    void fetchJsonTreatsNonStandard2xxStatusAsSuccess() throws Exception {
        // ステータスコード200以外を一律エラー扱いしていたため、201のような正常系も
        // 失敗として扱われていた。2xx全体を成功と判定できることを確認する。
        String responseBody = "{\"created\":true}";
        server.createContext("/create", exchange -> {
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(201, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        String result = fetcher.fetchJson("http://localhost:" + port + "/create");

        assertEquals(responseBody, result);
    }

    @Test
    void postJsonSendsBodyAndReturnsResponse() throws Exception {
        // POST・リクエストヘッダの付与が未対応だった学習テーマへの対応。
        String requestBody = "{\"name\":\"Alice\"}";
        String responseBody = "{\"id\":1}";
        server.createContext("/users", exchange -> {
            String actualMethod = exchange.getRequestMethod();
            String actualBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            byte[] bytes = (actualMethod.equals("POST") && actualBody.equals(requestBody))
                    ? responseBody.getBytes(StandardCharsets.UTF_8)
                    : "unexpected request".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        String result = fetcher.postJson("http://localhost:" + port + "/users", requestBody);

        assertEquals(responseBody, result);
    }

    @Test
    void fetchJsonWithHeadersSendsCustomHeader() throws Exception {
        server.createContext("/secure", exchange -> {
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            byte[] bytes = ("received:" + authHeader).getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        String result = fetcher.fetchJson(
                "http://localhost:" + port + "/secure", Map.of("Authorization", "Bearer token123"));

        assertEquals("received:Bearer token123", result);
    }

    @Test
    void buildUrlAppendsEncodedQueryParameters() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("q", "java 学習");
        params.put("page", "2");

        String url = JsonHttpFetcher.buildUrl("http://example.com/search", params);

        assertEquals("http://example.com/search?q=java+%E5%AD%A6%E7%BF%92&page=2", url);
    }

    @Test
    void fetchUserThrowsExceptionForMalformedJson() {
        // ステータスコードは200(通信自体は成功)だが、ボディがJSONとして解析できないケース。
        // fetchUser()内でのJackson変換時の失敗がHttpClientExceptionに変換されることを確認する。
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
