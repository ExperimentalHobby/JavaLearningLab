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
            username = in.readLine();
            if (username == null) {
                return;
            }
            send("SERVER: ようこそ、" + username + "さん");

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

    void send(String message) {
        out.println(message);
    }

    void close() {
        try {
            socket.close();
        } catch (IOException e) {
            // クローズ失敗は握りつぶしてよい(後始末処理のため)。
        }
    }
}
