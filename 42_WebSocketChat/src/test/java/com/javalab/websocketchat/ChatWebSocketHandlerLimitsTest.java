package com.javalab.websocketchat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 最大接続数の制限を検証するテスト。
 * 本番相当の100件では接続数が多すぎてテストが重くなるため、
 * {@code app.chat.max-connections}を2に差し替えた専用のSpringコンテキストで検証する。
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "app.chat.max-connections=2")
class ChatWebSocketHandlerLimitsTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ChatWebSocketHandler handler;

    private WebSocketSession connect(@NonNull TextWebSocketHandler clientHandler) throws Exception {
        StandardWebSocketClient client = new StandardWebSocketClient();
        return client.execute(clientHandler, "ws://localhost:" + port + "/chat").get(5, TimeUnit.SECONDS);
    }

    private static final class CloseStatusRecordingHandler extends TextWebSocketHandler {
        private final CountDownLatch closed = new CountDownLatch(1);
        private final AtomicReference<CloseStatus> closeStatus = new AtomicReference<>();

        @Override
        public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
            closeStatus.set(status);
            closed.countDown();
        }

        CloseStatus awaitCloseStatus() throws InterruptedException {
            assertTrue(closed.await(5, TimeUnit.SECONDS), "接続がクローズされませんでした");
            return closeStatus.get();
        }
    }

    @Test
    void connectionsBeyondMaxLimit_areClosedImmediately() throws Exception {
        // クローズコードは接続確立直後のタイミングで送るとクライアント側スタックを経由する間に
        // 正規化されることがあるため、ここでは「即座に切断される」という振る舞いのみを検証する。
        WebSocketSession session1 = connect(new RecordingWebSocketHandler());
        WebSocketSession session2 = connect(new RecordingWebSocketHandler());

        CloseStatusRecordingHandler overflowHandler = new CloseStatusRecordingHandler();
        connect(overflowHandler);

        overflowHandler.awaitCloseStatus();

        session1.close();
        session2.close();
    }

    @Test
    void sendHeartbeat_doesNotThrowAndKeepsSessionOpen() throws Exception {
        // PingMessageはSpringのWebSocketHandler抽象化ではハンドラメソッドとして観測しづらい
        // (下層のWebSocket実装が自動的にpong応答するため)。ここでは呼び出しが例外なく完了し、
        // 接続がそのまま維持されることを確認する回帰テストとして扱う。
        WebSocketSession session = connect(new RecordingWebSocketHandler());
        session.sendMessage(new TextMessage("太郎"));

        handler.sendHeartbeat();

        assertTrue(session.isOpen());

        session.close();
    }
}
