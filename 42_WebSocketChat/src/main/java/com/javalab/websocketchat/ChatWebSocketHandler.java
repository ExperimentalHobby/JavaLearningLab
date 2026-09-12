package com.javalab.websocketchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * チャットのWebSocketハンドラ。最初に受信したメッセージをユーザー名として登録し、
 * 以降のメッセージは{@code <ユーザー名>: <本文>}の形式で全参加者へブロードキャストする。
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> usernames = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        String payload = message.getPayload();

        String existingUsername = usernames.get(session.getId());
        if (existingUsername == null) {
            usernames.put(session.getId(), payload);
            broadcast(payload + "さんが参加しました");
            return;
        }

        broadcast(existingUsername + ": " + payload);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session.getId());
        String username = usernames.remove(session.getId());
        if (username != null) {
            broadcast(username + "さんが退出しました");
        }
    }

    private void broadcast(String text) {
        TextMessage message = new TextMessage(text);
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    // 1セッションへの送信失敗で他セッションへのブロードキャストを止めないよう、
                    // ログに残すのみで処理を継続する。
                    log.warn("メッセージ送信に失敗しました: sessionId={}", session.getId(), e);
                }
            }
        });
    }
}
