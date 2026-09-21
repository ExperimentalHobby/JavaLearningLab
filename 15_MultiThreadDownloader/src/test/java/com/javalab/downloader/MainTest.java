package com.javalab.downloader;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream)} のREPLループを結合テストするクラス。
 */
class MainTest {

    private HttpServer server;
    private int port;

    @TempDir
    Path tempDir;

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
    void runShowsDownloadResultForDownloadCommand() throws Exception {
        String content = "hello";
        server.createContext("/greeting.txt", exchange -> {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        String url = "http://localhost:" + port + "/greeting.txt";
        Scanner scanner = new Scanner("download " + url + " " + tempDir + "\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("成功"));
        assertTrue(Files.exists(tempDir.resolve("greeting.txt")));
        assertTrue(Files.readString(tempDir.resolve("greeting.txt")).equals(content));
    }

    @Test
    void runShowsErrorForInvalidUrlAndContinuesWithOtherUrls() throws Exception {
        // 不正なURL(URI.create()がIllegalArgumentExceptionを投げる文字を含む)を混在させても、
        // 以前はアプリ全体が落ちていた。その1件だけエラー表示し、他のURLは処理が継続することを確認する。
        String content = "hello";
        server.createContext("/greeting.txt", exchange -> {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        // "http://[invalid" はIPv6リテラルの閉じ括弧がなく、URI.create()がIllegalArgumentExceptionを
        // 投げる不正なURL。空白を含まない値でないとコマンドの引数分割(space区切り)が崩れるため、
        // 空白を含まない不正URLを選んでいる。
        String validUrl = "http://localhost:" + port + "/greeting.txt";
        Scanner scanner = new Scanner("download http://[invalid," + validUrl + " " + tempDir + "\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("エラー") || result.contains("不正"));
        assertTrue(result.contains("成功"));
    }

    @Test
    void runAvoidsOverwritingWhenMultipleUrlsShareSameFileName() throws Exception {
        // 異なるURLでもパスの末尾が同じだと保存ファイル名が衝突し、後勝ちで上書きされていた問題への対応。
        server.createContext("/dir1/data.txt", exchange -> {
            byte[] bytes = "from-dir1".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.createContext("/dir2/data.txt", exchange -> {
            byte[] bytes = "from-dir2".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();

        String url1 = "http://localhost:" + port + "/dir1/data.txt";
        String url2 = "http://localhost:" + port + "/dir2/data.txt";
        Scanner scanner = new Scanner("download " + url1 + "," + url2 + " " + tempDir + "\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        assertTrue(Files.exists(tempDir.resolve("data.txt")));
        assertTrue(Files.exists(tempDir.resolve("data (2).txt")));
    }

    @Test
    void runShowsErrorAndContinuesForUnknownCommand() {
        Scanner scanner = new Scanner("foobar\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("不明なコマンドです"));
    }
}
