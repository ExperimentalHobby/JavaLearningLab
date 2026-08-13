package com.javalab.socketchat;

import java.io.IOException;

/**
 * チャットサーバーの起動用エントリーポイント。
 */
public class Main {

    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        ChatServer server = new ChatServer(port);
        server.start();
        System.out.println("チャットサーバーを起動しました(ポート: " + server.port() + ")。Ctrl+Cで終了します。");
        // acceptループ用スレッドは非デーモンのため、main()がここで返ってもJVMは起動を続ける。
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }
}
