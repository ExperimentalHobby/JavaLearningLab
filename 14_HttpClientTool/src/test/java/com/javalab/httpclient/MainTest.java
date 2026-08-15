package com.javalab.httpclient;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream)} のREPLループを結合テストするクラス。
 * {@link JsonHttpFetcherTest}と同様、実際に起動したHTTPサーバーに対して通信する。
 */
class MainTest {

    private HttpServer server;
    private int port;

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
    void runShowsUserInfoForFetchUserCommand() {
        // fetchUserコマンドが正常に処理され、User.toString()相当のid/name/email形式で
        // 表示されることを確認する。
        String responseBody = "{\"id\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\"}";
        server.createContext("/user", exchange -> {
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        Scanner scanner = new Scanner("fetchUser http://localhost:" + port + "/user\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Alice"));
        assertTrue(result.contains("alice@example.com"));
    }

    @Test
    void runShowsErrorAndContinuesWhenConnectionFails() {
        String responseBody = "{\"id\":2,\"name\":\"Bob\",\"email\":\"bob@example.com\"}";
        server.createContext("/user", exchange -> {
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        // ".invalid" は名前解決に失敗する予約TLD。接続失敗後もループが継続し
        // 後続コマンドを正常に処理できることを確認する。
        Scanner scanner = new Scanner(
                "fetchUser http://this-host-does-not-exist.invalid/user\n"
                        + "fetchUser http://localhost:" + port + "/user\n"
                        + "exit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("エラー"));
        assertTrue(result.contains("Bob"));
        assertTrue(result.indexOf("エラー") < result.indexOf("Bob"),
                "接続失敗のエラー表示後もループが継続し、後続コマンドが処理されること");
    }
}
