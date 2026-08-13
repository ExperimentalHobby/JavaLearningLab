package com.javalab.socketchat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChatServerTest {

    private final ChatServer server = new ChatServer(0);

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void messageFromOneClientIsBroadcastToAnother() throws IOException {
        server.start();

        try (Socket clientA = new Socket("localhost", server.port());
             Socket clientB = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            PrintWriter outB = writer(clientB);
            BufferedReader inA = reader(clientA);
            BufferedReader inB = reader(clientB);

            // 最初の1行(ようこそメッセージ)を読むことで、サーバー側の登録完了を待ち合わせる。
            outA.println("Alice");
            inA.readLine();
            outB.println("Bob");
            inB.readLine();

            outA.println("こんにちは");

            assertEquals("Alice: こんにちは", inB.readLine());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void broadcastIsNotSentBackToSender() throws IOException {
        server.start();

        try (Socket clientA = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            BufferedReader inA = reader(clientA);

            outA.println("Alice");
            inA.readLine();

            outA.println("こんにちは");
            // 接続がAだけの状態でこのメッセージが読めてしまう場合、送信者自身にも
            // ブロードキャストされていることになる。読めずタイムアウトすることを期待する。
            clientA.setSoTimeout(500);

            assertThrows(SocketTimeoutException.class, inA::readLine);
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void broadcastReachesAllClientsExceptSenderWithThreeClients() throws IOException {
        server.start();

        try (Socket clientA = new Socket("localhost", server.port());
             Socket clientB = new Socket("localhost", server.port());
             Socket clientC = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            PrintWriter outB = writer(clientB);
            PrintWriter outC = writer(clientC);
            BufferedReader inA = reader(clientA);
            BufferedReader inB = reader(clientB);
            BufferedReader inC = reader(clientC);

            outA.println("Alice");
            inA.readLine();
            outB.println("Bob");
            inB.readLine();
            outC.println("Carol");
            inC.readLine();

            outA.println("こんにちは");

            assertEquals("Alice: こんにちは", inB.readLine());
            assertEquals("Alice: こんにちは", inC.readLine());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void communicationContinuesAfterOneClientDisconnects() throws IOException {
        server.start();

        try (Socket clientA = new Socket("localhost", server.port());
             Socket clientB = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            PrintWriter outB = writer(clientB);
            BufferedReader inA = reader(clientA);
            BufferedReader inB = reader(clientB);

            outA.println("Alice");
            inA.readLine();
            outB.println("Bob");
            inB.readLine();

            Socket clientC = new Socket("localhost", server.port());
            PrintWriter outC = writer(clientC);
            BufferedReader inC = reader(clientC);
            outC.println("Carol");
            inC.readLine();

            clientC.close();

            assertEquals("SERVER: Carolが退出しました", inB.readLine());

            outA.println("まだ話せますか");
            assertEquals("Alice: まだ話せますか", inB.readLine());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void stopClosesAllClientConnections() throws IOException {
        server.start();

        try (Socket clientA = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            BufferedReader inA = reader(clientA);
            outA.println("Alice");
            inA.readLine();

            server.stop();

            assertNull(inA.readLine());
        }
    }

    private static PrintWriter writer(Socket socket) throws IOException {
        return new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    private static BufferedReader reader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }
}
