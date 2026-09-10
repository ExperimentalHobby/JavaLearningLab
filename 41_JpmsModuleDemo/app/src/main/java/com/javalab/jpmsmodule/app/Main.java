package com.javalab.jpmsmodule.app;

import com.javalab.jpmsmodule.api.Greeter;
import com.javalab.jpmsmodule.impl.JapaneseGreeter;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * 挨拶メッセージ生成アプリのエントリーポイント。
 * {@code app}モジュールは{@code greeting-api}(インターフェース)と{@code greeting-impl}
 * (実装)の両方に{@code requires}しているが、{@code greeting-impl.internal}パッケージは
 * exportsされていないため参照できない(module-info.javaによる公開範囲制御の実演)。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner scanner, PrintStream out) {
        Greeter greeter = new JapaneseGreeter();

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
