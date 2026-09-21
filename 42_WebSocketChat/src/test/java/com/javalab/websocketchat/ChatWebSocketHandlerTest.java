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
    void join_isNotBroadcastToTheJoinerThemself() throws Exception {
        // 修正前は入室通知が本人にも届いていた(「太郎さんが参加しました」が太郎自身にも表示される)。
        RecordingWebSocketHandler handlerA = new RecordingWebSocketHandler();
        WebSocketSession sessionA = connect(handlerA);
        sessionA.sendMessage(new TextMessage("太郎"));

        RecordingWebSocketHandler handlerB = new RecordingWebSocketHandler();
        WebSocketSession sessionB = connect(handlerB);
        sessionB.sendMessage(new TextMessage("花子"));

        assertEquals("花子さんが参加しました", handlerA.awaitMessage());
        handlerB.assertNoMessageWithin(500);

        sessionA.close();
        sessionB.close();
    }

    @Test
    void chatMessage_isBroadcastToOtherParticipants() throws Exception {
        RecordingWebSocketHandler handlerA = new RecordingWebSocketHandler();
        WebSocketSession sessionA = connect(handlerA);
        sessionA.sendMessage(new TextMessage("太郎"));

        RecordingWebSocketHandler handlerB = new RecordingWebSocketHandler();
        WebSocketSession sessionB = connect(handlerB);
        sessionB.sendMessage(new TextMessage("花子"));
        handlerA.awaitMessage(); // 太郎に届く「花子さんが参加しました」を読み捨てる

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

        RecordingWebSocketHandler handlerB = new RecordingWebSocketHandler();
        WebSocketSession sessionB = connect(handlerB);
        sessionB.sendMessage(new TextMessage("花子"));
        handlerA.awaitMessage(); // 太郎に届く「花子さんが参加しました」を読み捨てる

        sessionB.close();

        assertEquals("花子さんが退出しました", handlerA.awaitMessage());
    }

    @Test
    void unregisteredSession_doesNotReceiveBroadcasts() throws Exception {
        // 修正前は、まだユーザー名を送っていない(未登録の)セッションにも他人の入退室通知が届いていた。
        RecordingWebSocketHandler handlerA = new RecordingWebSocketHandler();
        WebSocketSession sessionA = connect(handlerA); // ユーザー名を送らない

        RecordingWebSocketHandler handlerB = new RecordingWebSocketHandler();
        WebSocketSession sessionB = connect(handlerB);
        sessionB.sendMessage(new TextMessage("花子"));

        handlerA.assertNoMessageWithin(500);

        sessionA.close();
        sessionB.close();
    }

    @Test
    void blankUsername_showsErrorToSenderOnlyAndDoesNotRegister() throws Exception {
        RecordingWebSocketHandler handlerA = new RecordingWebSocketHandler();
        WebSocketSession sessionA = connect(handlerA);

        sessionA.sendMessage(new TextMessage("   "));

        assertEquals("エラー: ユーザー名を入力してください", handlerA.awaitMessage());

        sessionA.close();
    }

    @Test
    void tooLongUsername_showsErrorToSenderOnly() throws Exception {
        RecordingWebSocketHandler handlerA = new RecordingWebSocketHandler();
        WebSocketSession sessionA = connect(handlerA);

        sessionA.sendMessage(new TextMessage("あ".repeat(21)));

        assertEquals("エラー: ユーザー名は20文字以内で入力してください", handlerA.awaitMessage());

        sessionA.close();
    }

    @Test
    void duplicateUsername_showsErrorToSenderOnly() throws Exception {
        RecordingWebSocketHandler handlerA = new RecordingWebSocketHandler();
        WebSocketSession sessionA = connect(handlerA);
        sessionA.sendMessage(new TextMessage("太郎"));

        RecordingWebSocketHandler handlerB = new RecordingWebSocketHandler();
        WebSocketSession sessionB = connect(handlerB);
        sessionB.sendMessage(new TextMessage("太郎"));

        assertEquals("エラー: そのユーザー名は既に使用されています", handlerB.awaitMessage());

        sessionA.close();
        sessionB.close();
    }

    @Test
    void tooLongChatMessage_showsErrorToSenderOnly() throws Exception {
        RecordingWebSocketHandler handlerA = new RecordingWebSocketHandler();
        WebSocketSession sessionA = connect(handlerA);
        sessionA.sendMessage(new TextMessage("太郎"));

        sessionA.sendMessage(new TextMessage("あ".repeat(501)));

        assertEquals("エラー: メッセージは500文字以内で入力してください", handlerA.awaitMessage());

        sessionA.close();
    }
}
