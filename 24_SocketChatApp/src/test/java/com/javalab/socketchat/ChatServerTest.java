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

/**
 * {@link ChatServer} のマルチクライアント間ブロードキャストを検証するテスト。
 * モックは使わず、実際に{@link Socket}で複数のTCP接続を張って本物の通信を行う。
 * ポート0で起動して空きポートをOSに自動割当させ、テスト同士のポート競合を避けている。
 */
class ChatServerTest {

    private final ChatServer server = new ChatServer(0);

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void messageFromOneClientIsBroadcastToAnother() throws IOException {
        // AliceがBobと同じサーバーに接続した状態でメッセージを送ると、Bob側で
        // "Alice: <メッセージ>" 形式で受信できることを確認する(最も基本的なブロードキャスト)。
        server.start();

        // Aliceの登録が完了してからBobを接続する。両方を先に接続してしまうと、Bobの
        // ソケットは(まだ自分の登録が済んでいなくても)server.clientsに追加済みのため、
        // Aliceの入室通知ブロードキャストをBobが受け取ってしまい、後続のreadLine()の
        // 期待値がずれてしまう(入室通知を全員に届けるようにした本Issueの修正で顕在化した)。
        try (Socket clientA = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            BufferedReader inA = reader(clientA);
            outA.println("Alice");
            inA.readLine();

            try (Socket clientB = new Socket("localhost", server.port())) {
                PrintWriter outB = writer(clientB);
                BufferedReader inB = reader(clientB);
                outB.println("Bob");
                inB.readLine();

                outA.println("こんにちは");

                assertEquals("Alice: こんにちは", inB.readLine());
            }
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
        // 3人接続時、Aliceが送ったメッセージがBob・Carol両方に届くことを確認する
        // (2人限定ではなく任意人数へブロードキャストできることの検証)。
        server.start();

        // 各クライアントの登録が完了してから次を接続する(理由は
        // messageFromOneClientIsBroadcastToAnotherのコメントを参照)。
        try (Socket clientA = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            BufferedReader inA = reader(clientA);
            outA.println("Alice");
            inA.readLine();

            try (Socket clientB = new Socket("localhost", server.port())) {
                PrintWriter outB = writer(clientB);
                BufferedReader inB = reader(clientB);
                outB.println("Bob");
                inB.readLine();

                try (Socket clientC = new Socket("localhost", server.port())) {
                    PrintWriter outC = writer(clientC);
                    BufferedReader inC = reader(clientC);
                    outC.println("Carol");
                    inC.readLine();
                    // Carol入室時の通知をBobが受け取り済みの状態にしてから本題のメッセージを送る。
                    assertEquals("SERVER: Carolが入室しました", inB.readLine());

                    outA.println("こんにちは");

                    assertEquals("Alice: こんにちは", inB.readLine());
                    assertEquals("Alice: こんにちは", inC.readLine());
                }
            }
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void communicationContinuesAfterOneClientDisconnects() throws IOException {
        // Carolが退出した後も、Alice・Bob間の通信が引き続き正常に行えることを確認する
        // (1クライアントの切断がサーバー全体や他クライアントの接続に影響しないことの検証)。
        server.start();

        try (Socket clientA = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            BufferedReader inA = reader(clientA);
            outA.println("Alice");
            inA.readLine();

            try (Socket clientB = new Socket("localhost", server.port())) {
                PrintWriter outB = writer(clientB);
                BufferedReader inB = reader(clientB);
                outB.println("Bob");
                inB.readLine();

                Socket clientC = new Socket("localhost", server.port());
                PrintWriter outC = writer(clientC);
                BufferedReader inC = reader(clientC);
                outC.println("Carol");
                inC.readLine();
                // Carol入室時の通知をBobが受け取り済みの状態にしてから退出させる。
                assertEquals("SERVER: Carolが入室しました", inB.readLine());

                clientC.close();

                assertEquals("SERVER: Carolが退出しました", inB.readLine());

                outA.println("まだ話せますか");
                assertEquals("Alice: まだ話せますか", inB.readLine());
            }
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void stopClosesAllClientConnections() throws IOException {
        // server.stop()を呼ぶと接続中のソケットも強制的にクローズされ、クライアント側の
        // readLine()がストリーム終端(null)を返すことを確認する。
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

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void blankUsernameIsRejectedAndConnectionIsClosed() throws IOException {
        server.start();

        try (Socket clientA = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            BufferedReader inA = reader(clientA);

            outA.println("   ");

            assertEquals("SERVER: ユーザー名を空にすることはできません", inA.readLine());
            assertNull(inA.readLine());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void tooLongUsernameIsRejectedAndConnectionIsClosed() throws IOException {
        server.start();
        String tooLong = "a".repeat(21);

        try (Socket clientA = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            BufferedReader inA = reader(clientA);

            outA.println(tooLong);

            assertEquals("SERVER: ユーザー名は20文字以内で入力してください", inA.readLine());
            assertNull(inA.readLine());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void duplicateUsernameIsRejectedAndConnectionIsClosed() throws IOException {
        server.start();

        try (Socket clientA = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            BufferedReader inA = reader(clientA);
            outA.println("Alice");
            inA.readLine();

            try (Socket clientB = new Socket("localhost", server.port())) {
                PrintWriter outB = writer(clientB);
                BufferedReader inB = reader(clientB);

                outB.println("Alice");

                assertEquals("SERVER: そのユーザー名は既に使用されています: Alice", inB.readLine());
                assertNull(inB.readLine());
            }
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void communicationContinuesAfterOneClientConnectionIsAbruptlyReset() throws IOException {
        // PrintWriterはIOExceptionを握り潰す仕様でcheckError()を確認していないと、
        // 切断済みクライアントへ送り続けてしまう問題への対応。SO_LINGER(0)で
        // 正常なFINではなくRSTによる異常切断を発生させ、その後もサーバーが
        // 他クライアント間の通信を問題なく継続できることを確認する。
        server.start();

        try (Socket clientA = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            BufferedReader inA = reader(clientA);
            outA.println("Alice");
            inA.readLine();

            Socket clientB = new Socket("localhost", server.port());
            PrintWriter outB = writer(clientB);
            BufferedReader inB = reader(clientB);
            outB.println("Bob");
            inB.readLine();
            assertEquals("SERVER: Bobが入室しました", inA.readLine());

            // Bobの接続を正常なクローズ(FIN)ではなく、SO_LINGER(0)によるRSTで異常切断する。
            clientB.setSoLinger(true, 0);
            clientB.close();

            try (Socket clientC = new Socket("localhost", server.port())) {
                PrintWriter outC = writer(clientC);
                BufferedReader inC = reader(clientC);
                outC.println("Carol");
                inC.readLine();

                outA.println("まだ話せますか");
                assertEquals("Alice: まだ話せますか", inC.readLine());
            }
        }
    }

    @Test
    void portThrowsIllegalStateExceptionBeforeStart() {
        // start()を呼ぶ前はserverSocketがnullのためNPEになっていた問題への対応。
        // 「起動前に呼んだ」ことが分かる明示的な例外にする。
        assertThrows(IllegalStateException.class, server::port);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void joinNotificationIsBroadcastToExistingParticipants() throws IOException {
        // 入室通知が本人にしか届かず、既存の参加者は誰が入ってきたか分からなかった問題への対応。
        server.start();

        try (Socket clientA = new Socket("localhost", server.port())) {
            PrintWriter outA = writer(clientA);
            BufferedReader inA = reader(clientA);
            outA.println("Alice");
            inA.readLine();
            // 修正前は入室通知が本人にしか届かず、このreadLine()が永久にブロックしてしまう
            // (ブロッキングソケット読み取りは@Timeoutでは中断できないため)。安全のため
            // タイムアウトを設定し、Red確認時はハングせずSocketTimeoutExceptionで失敗させる。
            clientA.setSoTimeout(3000);

            Socket clientB = new Socket("localhost", server.port());
            PrintWriter outB = writer(clientB);
            outB.println("Bob");

            assertEquals("SERVER: Bobが入室しました", inA.readLine());

            clientB.close();
        }
    }

    private static PrintWriter writer(Socket socket) throws IOException {
        return new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    private static BufferedReader reader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }
}
