package com.javalab.websocketchat;

import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * テスト用のWebSocketクライアントハンドラ。受信したメッセージを{@link BlockingQueue}に積み、
 * {@link #awaitMessage()}でタイムアウト付きにポーリング取得できるようにすることで、
 * サーバーからの非同期な配信をsleepなしで確定的に検証できるようにする。
 */
class RecordingWebSocketHandler extends TextWebSocketHandler {

    private final BlockingQueue<String> messages = new LinkedBlockingQueue<>();

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        messages.add(message.getPayload());
    }

    String awaitMessage() throws InterruptedException {
        String message = messages.poll(3, TimeUnit.SECONDS);
        if (message == null) {
            throw new AssertionError("タイムアウト: メッセージを受信できませんでした");
        }
        return message;
    }

    /** 短い待機時間内にメッセージが届かないことを確認する(「届かないこと」の確定的な検証は原理的に不可能なため、目安の待機)。 */
    void assertNoMessageWithin(long timeoutMillis) throws InterruptedException {
        assertNull(messages.poll(timeoutMillis, TimeUnit.MILLISECONDS));
    }
}
