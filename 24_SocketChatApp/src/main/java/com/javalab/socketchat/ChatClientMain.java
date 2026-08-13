package com.javalab.socketchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 手動確認用の簡易コンソールチャットクライアント。
 * 標準入力の各行をサーバーへ送信しつつ、サーバーからの受信メッセージを別スレッドで表示する。
 */
public class ChatClientMain {

    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        Socket socket = new Socket(host, port);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        System.out.print("ユーザー名を入力してください: ");
        out.println(scanner.nextLine());

        // 受信専用スレッド: サーバーからの行を読み次第すぐ表示する(送信入力待ちでブロックしないため)。
        Thread receiver = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                // 接続断による終了は正常経路として扱う。
            }
        });
        receiver.setDaemon(true);
        receiver.start();

        while (scanner.hasNextLine()) {
            out.println(scanner.nextLine());
        }
        socket.close();
    }
}
