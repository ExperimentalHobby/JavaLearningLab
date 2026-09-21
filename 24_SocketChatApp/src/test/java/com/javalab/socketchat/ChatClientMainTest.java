package com.javalab.socketchat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ChatClientMain#run(Socket, Scanner, PrintStream)} のサーバー切断検知を検証するクラス。
 * モックは使わず、実際に{@link ChatServer}を起動してTCP接続を張り、サーバーを止めることで
 * 本物の切断を発生させる。
 */
class ChatClientMainTest {

    private final ChatServer server = new ChatServer(0);

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void runDetectsServerDisconnectionAndStopsWithoutHanging() throws Exception {
        // 修正前は受信スレッドが黙って終了し、メインループは切れたソケットに入力を送り続けていた。
        // サーバーを止めて接続を切断した後、クライアントが「切断されました」と表示して
        // ハングせずにrun()を終了することを確認する。
        server.start();
        Socket socket = new Socket("localhost", server.port());

        // Scanner.hasNextLine()がブロックしたまま新しい行を後から流し込めるよう、
        // PipedInputStreamで「ユーザーがまだ入力中」の状態を再現する。
        PipedOutputStream pipedOut = new PipedOutputStream();
        PipedInputStream pipedIn = new PipedInputStream(pipedOut);
        PrintWriter userInput = new PrintWriter(pipedOut, true);
        Scanner scanner = new Scanner(pipedIn, StandardCharsets.UTF_8);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        userInput.println("TestUser");

        Thread clientThread = new Thread(() -> {
            try {
                ChatClientMain.run(socket, scanner, out);
            } catch (IOException e) {
                // ソケットクローズ後の書き込みなどは許容する。
            }
        });
        clientThread.start();

        waitUntil(() -> buffer.toString(StandardCharsets.UTF_8).contains("ようこそ、TestUserさん"));

        server.stop();
        waitUntil(() -> buffer.toString(StandardCharsets.UTF_8).contains("サーバーとの接続が切断されました"));

        // 切断後もユーザーが入力を続けるケース。送信ループがcheckError()で異常終了を検知し、
        // 正常にrun()を抜けられることを確認する(検知できないとハングし続ける)。
        // TCPの仕様上、切断後の最初の書き込みはローカルの送信バッファに乗って成功してしまうことが
        // あり、エラーとして検知されるのは大抵2回目以降の書き込みになるため、間隔を空けて複数回送る。
        for (int i = 0; i < 5 && clientThread.isAlive(); i++) {
            userInput.println("切断後のメッセージ" + i);
            Thread.sleep(200);
        }

        clientThread.join(5000);

        assertFalse(clientThread.isAlive());
        assertTrue(buffer.toString(StandardCharsets.UTF_8).contains("サーバーとの接続が切断されました"));
    }

    private static void waitUntil(java.util.function.BooleanSupplier condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5000;
        while (!condition.getAsBoolean() && System.currentTimeMillis() < deadline) {
            Thread.sleep(20);
        }
    }
}
