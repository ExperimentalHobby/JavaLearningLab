package com.javalab.socketchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 1クライアント接続を担当するハンドラ。接続直後の最初の行をユーザー名として扱い、
 * 以後の各行を他クライアントへブロードキャストする。{@link ChatServer}によりスレッドプールで実行される。
 */
class ClientHandler implements Runnable {

    private static final int MAX_USERNAME_LENGTH = 20;

    private final Socket socket;
    private final ChatServer server;
    private final PrintWriter out;
    private String username;

    ClientHandler(Socket socket, ChatServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            String requestedUsername = in.readLine();
            if (requestedUsername == null) {
                return;
            }
            requestedUsername = requestedUsername.trim();
            if (requestedUsername.isEmpty()) {
                send("SERVER: ユーザー名を空にすることはできません");
                return;
            }
            if (requestedUsername.length() > MAX_USERNAME_LENGTH) {
                send("SERVER: ユーザー名は" + MAX_USERNAME_LENGTH + "文字以内で入力してください");
                return;
            }
            if (server.isUsernameTaken(requestedUsername)) {
                send("SERVER: そのユーザー名は既に使用されています: " + requestedUsername);
                return;
            }
            username = requestedUsername;
            send("SERVER: ようこそ、" + username + "さん");
            // 退出時はserver.broadcast()で全員に通知しているのに対し、入室時は本人にしか
            // 通知していなかったため、既存の参加者は誰が入ってきたか分からなかった問題への対応。
            server.broadcast("SERVER: " + username + "が入室しました", this);

            String line;
            while ((line = in.readLine()) != null) {
                server.broadcast(username + ": " + line, this);
            }
        } catch (IOException e) {
            // 接続断は正常な終了経路として扱う(後始末はfinallyに委ねる)。
        } finally {
            server.remove(this);
            if (username != null) {
                server.broadcast("SERVER: " + username + "が退出しました", this);
            }
            close();
        }
    }

    String username() {
        return username;
    }

    void send(String message) {
        out.println(message);
        if (out.checkError()) {
            // PrintWriterはIOExceptionを握り潰す仕様のため、checkError()で送信失敗を検知する。
            // 検知したら自ら接続をクローズすることで、切断済みクライアントへ送り続けるのを防ぐ。
            // クローズにより、このクライアント自身のrun()内のin.readLine()もIOException/EOFとなり、
            // finallyブロックで通常の退出処理(server.remove・退出通知)が行われる。
            close();
        }
    }

    void close() {
        try {
            socket.close();
        } catch (IOException e) {
            // クローズ失敗は握りつぶしてよい(後始末処理のため)。
        }
    }
}
