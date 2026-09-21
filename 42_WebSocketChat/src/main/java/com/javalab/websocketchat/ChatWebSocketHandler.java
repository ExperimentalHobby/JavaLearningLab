package com.javalab.websocketchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * チャットのWebSocketハンドラ。最初に受信したメッセージをユーザー名として登録し、
 * 以降のメッセージは{@code <ユーザー名>: <本文>}の形式で参加者へブロードキャストする。
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MAX_MESSAGE_LENGTH = 500;

    // WebSocketSession#sendMessageはスレッドセーフではない。複数の送信元スレッドから同じセッションへ
    // 同時に送信されうるため、ConcurrentWebSocketSessionDecoratorでラップして排他制御する。
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    // ユーザー名が登録済みのセッションのみがブロードキャストの対象になる。
    private final Map<String, String> usernames = new ConcurrentHashMap<>();
    // テストで小さい値に差し替えられるよう@Valueで注入可能にしている(デフォルトは本番相当の100)。
    private final int maxConnections;

    public ChatWebSocketHandler(@Value("${app.chat.max-connections:100}") int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws IOException {
        if (sessions.size() >= maxConnections) {
            session.close(CloseStatus.SERVICE_OVERLOAD.withReason("最大接続数に達しました"));
            return;
        }
        sessions.put(session.getId(), new ConcurrentWebSocketSessionDecorator(session, 5_000, 8_192));
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession rawSession, @NonNull TextMessage message) {
        WebSocketSession session = sessions.get(rawSession.getId());
        if (session == null) {
            return;
        }
        String payload = message.getPayload();

        String existingUsername = usernames.get(session.getId());
        if (existingUsername == null) {
            registerUsername(session, payload);
            return;
        }

        if (payload.length() > MAX_MESSAGE_LENGTH) {
            sendTo(session, "エラー: メッセージは" + MAX_MESSAGE_LENGTH + "文字以内で入力してください");
            return;
        }

        broadcast(existingUsername + ": " + payload, null);
    }

    private void registerUsername(WebSocketSession session, String username) {
        if (username.isBlank()) {
            sendTo(session, "エラー: ユーザー名を入力してください");
            return;
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            sendTo(session, "エラー: ユーザー名は" + MAX_USERNAME_LENGTH + "文字以内で入力してください");
            return;
        }
        if (usernames.containsValue(username)) {
            sendTo(session, "エラー: そのユーザー名は既に使用されています");
            return;
        }

        usernames.put(session.getId(), username);
        // 入室通知は本人以外へ送る。本人は自分が参加したことを既に知っているため。
        broadcast(username + "さんが参加しました", session.getId());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession rawSession, @NonNull CloseStatus status) {
        sessions.remove(rawSession.getId());
        String username = usernames.remove(rawSession.getId());
        if (username != null) {
            broadcast(username + "さんが退出しました", null);
        }
    }

    /** 定期的にping送信し、生きているコネクションを維持する(死活監視)。 */
    @Scheduled(fixedRate = 30_000)
    void sendHeartbeat() {
        PingMessage ping = new PingMessage();
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(ping);
                } catch (IOException e) {
                    log.warn("ハートビート送信に失敗しました: sessionId={}", session.getId(), e);
                }
            }
        });
    }

    private void sendTo(WebSocketSession session, String text) {
        try {
            session.sendMessage(new TextMessage(text));
        } catch (IOException e) {
            log.warn("メッセージ送信に失敗しました: sessionId={}", session.getId(), e);
        }
    }

    /**
     * ユーザー名が登録済みのセッションのみへブロードキャストする(未登録セッションは対象外)。
     * @param excludeSessionId ブロードキャスト対象から除外するセッションID(不要な場合は{@code null})
     */
    private void broadcast(String text, String excludeSessionId) {
        TextMessage message = new TextMessage(text);
        usernames.keySet().stream()
                .filter(id -> !id.equals(excludeSessionId))
                .map(sessions::get)
                .filter(Objects::nonNull)
                .forEach(session -> {
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
