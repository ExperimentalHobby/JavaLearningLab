package com.javalab.httpclient;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * HTTPクライアントツールのエントリーポイント。
 * 標準入力からコマンドを読み取り、指定URLへJSONを取得しにいく対話型REPLを提供する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}を差し替えられるよう分離している。
     * コマンド: {@code fetch <URL>}(生JSON表示) / {@code fetchUser <URL>}(User解析表示) / {@code exit}。
     * 通信エラーはループを止めずエラー表示のみ行い、次のコマンド入力を継続する。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        JsonHttpFetcher fetcher = new JsonHttpFetcher();
        out.println("HTTPクライアントツール。コマンド: fetch <URL> / fetchUser <URL> / exit");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+", 2);
            String command = parts[0];
            if (command.equals("exit")) {
                break;
            } else if (command.equals("fetch") && parts.length == 2) {
                try {
                    out.println(fetcher.fetchJson(parts[1]));
                } catch (HttpClientException e) {
                    out.println("エラー: " + e.getMessage());
                }
            } else if (command.equals("fetchUser") && parts.length == 2) {
                try {
                    User user = fetcher.fetchUser(parts[1]);
                    out.println("id=" + user.id() + ", name=" + user.name() + ", email=" + user.email());
                } catch (HttpClientException e) {
                    out.println("エラー: " + e.getMessage());
                }
            } else {
                out.println("不明なコマンドです: " + line);
            }
        }
    }
}
