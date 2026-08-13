package com.javalab.socketchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TCPソケットで複数クライアントを受け付け、チャットメッセージをブロードキャストするサーバー。
 * 接続ごとに{@link ClientHandler}を生成し、スレッドプールで並行処理する。
 */
public class ChatServer {

    private final int requestedPort;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final ExecutorService clientExecutor = Executors.newCachedThreadPool();

    private ServerSocket serverSocket;
    private Thread acceptThread;

    /**
     * @param port 待ち受けポート。{@code 0}を指定するとOSが空きポートを自動割り当てする
     */
    public ChatServer(int port) {
        this.requestedPort = port;
    }

    /**
     * {@link ServerSocket}を開き、クライアント接続の受け付けを別スレッドで開始する。
     * @throws IOException ポートのバインドに失敗した場合
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(requestedPort);
        acceptThread = new Thread(this::acceptLoop, "chat-server-accept");
        acceptThread.start();
    }

    private void acceptLoop() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                clients.add(handler);
                clientExecutor.execute(handler);
            } catch (IOException e) {
                // stop()によるServerSocketのクローズで発生する例外はループ終了の合図なので無視する。
                break;
            }
        }
    }

    /**
     * 実際にバインドされたポート番号を返す。
     * @return バインド済みポート番号
     */
    public int port() {
        return serverSocket.getLocalPort();
    }

    void remove(ClientHandler handler) {
        clients.remove(handler);
    }

    void broadcast(String message, ClientHandler exclude) {
        for (ClientHandler client : clients) {
            if (client != exclude) {
                client.send(message);
            }
        }
    }

    /**
     * サーバーを停止する。{@link ServerSocket}と全クライアント接続をクローズし、スレッドプールをシャットダウンする。
     */
    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // クローズ失敗は握りつぶしてよい(サーバー停止処理のため)。
        }
        for (ClientHandler client : clients) {
            client.close();
        }
        clients.clear();
        clientExecutor.shutdownNow();
    }
}
