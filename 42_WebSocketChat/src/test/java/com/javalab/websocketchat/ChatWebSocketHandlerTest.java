package com.javalab.websocketchat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link ChatWebSocketHandler}をHTTP経由で結合テストするクラス。
 * {@code webEnvironment = RANDOM_PORT}により実際に組み込みTomcatを起動し、
 * {@link StandardWebSocketClient}(Java標準WebSocketクライアントAPI)で実際に接続する
 * (モックは使わない、既存Issueと同じ「実リソースでのテスト」方針)。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatWebSocketHandlerTest {

    @LocalServerPort
    private int port;

    private WebSocketSession connect(RecordingWebSocketHandler handler) throws Exception {
        StandardWebSocketClient client = new StandardWebSocketClient();
        return client.execute(handler, "ws://localhost:" + port + "/chat").get(5, TimeUnit.SECONDS);
    }

    @Test
    void join_broadcastsJoinMessage() throws Exception {
        RecordingWebSocketHandler handler = new RecordingWebSocketHandler();
        WebSocketSession session = connect(handler);

        session.sendMessage(new TextMessage("太郎"));

        assertEquals("太郎さんが参加しました", handler.awaitMessage());

        session.close();
    }

    @Test
    void chatMessage_isBroadcastToOtherParticipants() throws Exception {
        RecordingWebSocketHandler handlerA = new RecordingWebSocketHandler();
        WebSocketSession sessionA = connect(handlerA);
        sessionA.sendMessage(new TextMessage("太郎"));
        handlerA.awaitMessage(); // 自分自身の参加通知を読み捨てる

        RecordingWebSocketHandler handlerB = new RecordingWebSocketHandler();
        WebSocketSession sessionB = connect(handlerB);
        sessionB.sendMessage(new TextMessage("花子"));
        handlerA.awaitMessage(); // 太郎に届く「花子さんが参加しました」を読み捨てる
        handlerB.awaitMessage(); // 花子自身の参加通知を読み捨てる

        sessionA.sendMessage(new TextMessage("こんにちは"));

        assertEquals("太郎: こんにちは", handlerB.awaitMessage());

        sessionA.close();
        sessionB.close();
    }

    @Test
    void disconnect_broadcastsLeaveMessageToRemainingParticipants() throws Exception {
        RecordingWebSocketHandler handlerA = new RecordingWebSocketHandler();
        WebSocketSession sessionA = connect(handlerA);
        sessionA.sendMessage(new TextMessage("太郎"));
        handlerA.awaitMessage(); // 自分自身の参加通知を読み捨てる

        RecordingWebSocketHandler handlerB = new RecordingWebSocketHandler();
        WebSocketSession sessionB = connect(handlerB);
        sessionB.sendMessage(new TextMessage("花子"));
        handlerA.awaitMessage(); // 太郎に届く「花子さんが参加しました」を読み捨てる
        handlerB.awaitMessage(); // 花子自身の参加通知を読み捨てる

        sessionB.close();

        assertEquals("花子さんが退出しました", handlerA.awaitMessage());
    }
}
