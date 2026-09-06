package com.javalab.modernjavasyntax;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * 注文配送状況トラッキングツールのエントリーポイント。
 * 標準入力からコマンド(place/ship/deliver/cancel/status/list)を1行ずつ読み取り、結果を標準出力に表示する。
 * {@code exit} で終了する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out, LocalDate::now);
    }

    /**
     * REPLループ本体。{@code today}を注入可能にすることで、テストから日付を固定できるようにしている。
     */
    static void run(Scanner scanner, PrintStream out, Supplier<LocalDate> today) {
        Map<String, OrderState> orders = new LinkedHashMap<>();

        out.println("注文配送状況トラッキングツールへようこそ。"
                + "コマンド: place <注文ID> / ship <注文ID> <伝票番号> / deliver <注文ID> / cancel <注文ID> <理由> / status <注文ID> / list / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(orders, line, out, today);
            } catch (IllegalStateException | IllegalArgumentException e) {
                // 不正な状態遷移・不明なコマンド・存在しない注文IDはクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleCommand(Map<String, OrderState> orders, String line, PrintStream out, Supplier<LocalDate> today) {
        String[] tokens = line.split("\\s+", 2);
        String command = tokens[0];
        String rest = tokens.length > 1 ? tokens[1] : "";

        switch (command) {
            case "place" -> {
                String orderId = requireSingleArg(rest, line);
                orders.put(orderId, new OrderState.Placed(today.get()));
                out.println("注文を受け付けました: " + orderId);
            }
            case "ship" -> {
                String[] args = requireTwoArgs(rest, line);
                OrderState current = requireOrder(orders, args[0]);
                orders.put(args[0], OrderStateTransition.ship(current, args[1], today.get()));
                out.println("発送しました: " + args[0]);
            }
            case "deliver" -> {
                String orderId = requireSingleArg(rest, line);
                OrderState current = requireOrder(orders, orderId);
                orders.put(orderId, OrderStateTransition.deliver(current, today.get()));
                out.println("配達完了にしました: " + orderId);
            }
            case "cancel" -> {
                String[] args = requireTwoArgs(rest, line);
                OrderState current = requireOrder(orders, args[0]);
                orders.put(args[0], OrderStateTransition.cancel(current, args[1]));
                out.println("キャンセルしました: " + args[0]);
            }
            case "status" -> {
                String orderId = requireSingleArg(rest, line);
                OrderState current = requireOrder(orders, orderId);
                out.print(OrderReceiptFormatter.format(orderId, current));
            }
            case "list" -> orders.keySet().forEach(out::println);
            default -> throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }

    private static String requireSingleArg(String rest, String line) {
        if (rest.isBlank()) {
            throw new IllegalArgumentException("入力形式が不正です: " + line);
        }
        return rest;
    }

    private static String[] requireTwoArgs(String rest, String line) {
        String[] args = rest.split("\\s+", 2);
        if (args.length != 2 || args[0].isBlank() || args[1].isBlank()) {
            throw new IllegalArgumentException("入力形式が不正です: " + line);
        }
        return args;
    }

    private static OrderState requireOrder(Map<String, OrderState> orders, String orderId) {
        OrderState state = orders.get(orderId);
        if (state == null) {
            throw new IllegalArgumentException("該当する注文がありません: " + orderId);
        }
        return state;
    }
}
