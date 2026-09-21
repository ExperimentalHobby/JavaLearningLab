package com.javalab.genericcollection;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * ジェネリクス活用ライブラリのデモ用エントリーポイント。
 * 標準入力からコマンドを読み取り、{@link GenericStack}/{@link GenericQueue}/{@link CollectionUtils}を
 * 操作する対話型REPLを提供する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}を差し替えられるよう分離している。
     * コマンド: {@code stack push/pop/peek/size <値>} / {@code queue enqueue/dequeue/peek/size <値>} /
     * {@code list add <値>} / {@code list max} / {@code list min} / {@code list filter <部分文字列>} /
     * {@code list map upper} / {@code exit}。
     * 空のスタック/キュー/リストへの操作はループを止めずエラー表示のみ行い、次のコマンド入力を継続する。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        GenericStack<String> stack = new GenericStack<>();
        GenericQueue<String> queue = new GenericQueue<>();
        List<String> list = new ArrayList<>();
        out.println("ジェネリクス活用ライブラリ。コマンド: stack push/pop/peek/size <値> / "
                + "queue enqueue/dequeue/peek/size <値> / list add <値> / list max / list min / "
                + "list filter <部分文字列> / list map upper / exit");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+", 3);
            String target = parts[0];
            try {
                if (target.equals("exit")) {
                    return;
                } else if (target.equals("stack") && parts.length >= 2) {
                    handleStack(stack, parts, out);
                } else if (target.equals("queue") && parts.length >= 2) {
                    handleQueue(queue, parts, out);
                } else if (target.equals("list") && parts.length >= 2) {
                    handleList(list, parts, out);
                } else {
                    out.println("不明なコマンドです: " + line);
                }
            } catch (EmptyCollectionException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleStack(GenericStack<String> stack, String[] parts, PrintStream out) {
        String operation = parts[1];
        switch (operation) {
            case "push" -> {
                if (parts.length != 3) {
                    out.println("使い方: stack push <値>");
                    return;
                }
                stack.push(parts[2]);
                out.println("push しました: " + parts[2]);
            }
            case "pop" -> out.println("pop しました: " + stack.pop());
            case "peek" -> out.println("peek: " + stack.peek());
            case "size" -> out.println("size: " + stack.size());
            default -> out.println("不明なstack操作です: " + operation);
        }
    }

    private static void handleQueue(GenericQueue<String> queue, String[] parts, PrintStream out) {
        String operation = parts[1];
        switch (operation) {
            case "enqueue" -> {
                if (parts.length != 3) {
                    out.println("使い方: queue enqueue <値>");
                    return;
                }
                queue.enqueue(parts[2]);
                out.println("enqueue しました: " + parts[2]);
            }
            case "dequeue" -> out.println("dequeue しました: " + queue.dequeue());
            case "peek" -> out.println("peek: " + queue.peek());
            case "size" -> out.println("size: " + queue.size());
            default -> out.println("不明なqueue操作です: " + operation);
        }
    }

    /**
     * {@link CollectionUtils}の汎用操作(max/min/filter/map)をCLIから呼び出せるようにするハンドラ。
     * @param list 操作対象のリスト
     * @param parts コマンドを分割した配列({@code parts[1]}が操作名)
     * @param out 出力先
     */
    private static void handleList(List<String> list, String[] parts, PrintStream out) {
        String operation = parts[1];
        switch (operation) {
            case "add" -> {
                if (parts.length != 3) {
                    out.println("使い方: list add <値>");
                    return;
                }
                list.add(parts[2]);
                out.println("add しました: " + parts[2]);
            }
            case "max" -> out.println("max: " + CollectionUtils.max(list));
            case "min" -> out.println("min: " + CollectionUtils.min(list));
            case "filter" -> {
                if (parts.length != 3) {
                    out.println("使い方: list filter <部分文字列>");
                    return;
                }
                String keyword = parts[2];
                out.println("filter: " + CollectionUtils.filter(list, value -> value.contains(keyword)));
            }
            case "map" -> {
                if (parts.length != 3 || !parts[2].equals("upper")) {
                    out.println("使い方: list map upper");
                    return;
                }
                out.println("map: " + CollectionUtils.map(list, value -> value.toUpperCase(Locale.ROOT)));
            }
            default -> out.println("不明なlist操作です: " + operation);
        }
    }
}
