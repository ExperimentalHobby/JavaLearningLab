package com.javalab.builderorder;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 注文管理REPLのエントリーポイント。
 * {@code start}で{@link Order.Builder}を開始し、{@code item}等で段階的に内容を追加、
 * {@code build}で確定するという流れでBuilderパターンによるオブジェクト構築を体験できる。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}を差し替えられるよう分離している。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        out.println("注文管理システム。コマンド: "
                + "start <顧客名> <配送先住所> / item <商品名> <数量> <単価> / "
                + "payment <方法> / wrap on|off / note <メモ> / build / exit");

        Order.Builder builder = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String command = parts[0];
            try {
                if (command.equals("exit")) {
                    return;
                }
                if (command.equals("start")) {
                    builder = new Order.Builder(parts[1], parts[2]);
                    out.println("注文を開始しました: " + parts[1] + " 様");
                    continue;
                }
                if (builder == null) {
                    out.println("エラー: 先にstartで注文を開始してください");
                    continue;
                }
                switch (command) {
                    case "item" -> {
                        builder.addItem(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                        out.println("商品を追加しました: " + parts[1]);
                    }
                    case "payment" -> {
                        builder.paymentMethod(parts[1]);
                        out.println("支払方法を設定しました: " + parts[1]);
                    }
                    case "wrap" -> {
                        builder.giftWrap(parts[1].equals("on"));
                        out.println("ギフトラッピングを設定しました: " + parts[1]);
                    }
                    case "note" -> {
                        String noteText = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
                        builder.note(noteText);
                        out.println("メモを設定しました: " + noteText);
                    }
                    case "build" -> {
                        Order order = builder.build();
                        out.println(order.toSummary());
                        builder = null;
                    }
                    default -> out.println("不明なコマンドです: " + line);
                }
            } catch (RuntimeException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }
}
