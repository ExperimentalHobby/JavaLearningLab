package com.javalab.socketchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
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

        try (Socket socket = new Socket(host, port)) {
            run(socket, new Scanner(System.in, StandardCharsets.UTF_8), System.out);
        }
    }

    /**
     * クライアントのメインループ本体。テストから{@link Socket}/{@link Scanner}/{@link PrintStream}を
     * 差し替えられるよう分離している。
     * @param socket 接続済みのサーバーソケット
     * @param scanner ユーザー名・送信メッセージの読み取り元
     * @param out 受信メッセージ・状態表示の出力先
     * @throws IOException ソケットのストリーム取得に失敗した場合
     */
    static void run(Socket socket, Scanner scanner, PrintStream out) throws IOException {
        PrintWriter socketOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        out.print("ユーザー名を入力してください: ");
        socketOut.println(scanner.nextLine());

        // 受信専用スレッド: サーバーからの行を読み次第すぐ表示する(送信入力待ちでブロックしないため)。
        // サーバー切断(ストリーム終端)を検知した場合はその旨を表示する
        // (修正前は黙って終了し、利用者がサーバー切断に気付けなかった)。
        Thread receiver = new Thread(() -> {
            try {
                String line;
                while ((line = socketIn.readLine()) != null) {
                    out.println(line);
                }
            } catch (IOException e) {
                // 接続断による終了は正常経路として扱う(下のメッセージ表示に処理を委ねる)。
            }
            out.println("サーバーとの接続が切断されました");
        });
        receiver.setDaemon(true);
        receiver.start();

        while (scanner.hasNextLine()) {
            socketOut.println(scanner.nextLine());
            if (socketOut.checkError()) {
                // PrintWriterはIOExceptionを握り潰す仕様のため、checkError()で送信失敗
                // (サーバー切断済みのソケットへの書き込み)を検知し、送信ループを抜ける
                // (修正前はメインループが切れたソケットに入力を送り続けていた)。
                break;
            }
        }
    }
}
