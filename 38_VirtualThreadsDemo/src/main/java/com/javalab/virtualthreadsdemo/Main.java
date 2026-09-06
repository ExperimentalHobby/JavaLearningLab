package com.javalab.virtualthreadsdemo;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * 複数URLへの同時疎通確認ツールのエントリーポイント。
 * 標準入力からコマンド(check/checkPlatform/exit)を1行ずつ読み取り、結果を標準出力に表示する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner scanner, PrintStream out) {
        out.println("URL疎通確認ツール(Virtual Threads Demo)へようこそ。"
                + "コマンド: check <url...> / checkPlatform <poolSize> <url...> / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(line, out);
            } catch (IllegalArgumentException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleCommand(String line, PrintStream out) {
        if (line.startsWith("checkPlatform ")) {
            String[] parts = line.substring(14).trim().split("\\s+");
            if (parts.length < 2) {
                throw new IllegalArgumentException("入力形式が不正です: " + line);
            }
            int poolSize = parsePoolSize(parts[0], line);
            List<String> urls = Arrays.asList(parts).subList(1, parts.length);
            printResults(new PlatformThreadEndpointChecker(poolSize).checkAll(urls), out);
        } else if (line.startsWith("check ")) {
            String[] parts = line.substring(6).trim().split("\\s+");
            if (parts.length < 1 || parts[0].isBlank()) {
                throw new IllegalArgumentException("入力形式が不正です: " + line);
            }
            printResults(new VirtualThreadEndpointChecker().checkAll(List.of(parts)), out);
        } else {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }

    private static int parsePoolSize(String text, String line) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("入力形式が不正です: " + line);
        }
    }

    private static void printResults(List<CheckResult> results, PrintStream out) {
        results.forEach(r -> out.println(r.url() + " -> " + r.statusCode()));
    }
}
