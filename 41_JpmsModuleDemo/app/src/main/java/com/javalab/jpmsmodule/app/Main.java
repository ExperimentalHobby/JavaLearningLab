package com.javalab.jpmsmodule.app;

import com.javalab.jpmsmodule.api.Greeter;

import java.io.PrintStream;
import java.util.ServiceLoader;
import java.util.Scanner;

/**
 * 挨拶メッセージ生成アプリのエントリーポイント。
 * {@code app}モジュールは{@code greeting-api}(インターフェース)にのみ{@code requires}し、
 * 実装は{@link ServiceLoader}経由で取得する({@code greeting-impl}を型として直接参照しない)。
 * これにより「APIモジュールだけに依存し、実装は差し替え可能にする」というmodule-infoの主目的を
 * 実演している(module-info.javaの{@code uses}/{@code provides}参照)。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner scanner, PrintStream out) {
        Greeter greeter = ServiceLoader.load(Greeter.class).findFirst()
                .orElseThrow(() -> new IllegalStateException("Greeterの実装が見つかりません"));

        out.println("JPMSモジュールデモへようこそ。コマンド: greet <name> / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            if (line.startsWith("greet ")) {
                out.println(greeter.greet(line.substring(6).trim()));
            } else {
                out.println("エラー: 不明なコマンドです: " + line);
            }
        }
    }
}
