package com.javalab.streamapi;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 対話式CLIデータ集計ツールのエントリーポイント。
 * 標準入力からコマンド({@code add}/{@code list}/{@code byCategory})を1行ずつ読み取り、
 * {@link SalesAggregator} を使って集計結果を表示する。{@code exit} で終了する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。{@link Scanner}/{@link PrintStream} を引数として受け取ることで、
     * テストから {@code StringReader}/{@code ByteArrayOutputStream} を注入できるようにしている。
     * @param scanner 入力読み取り元
     * @param out 出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        List<SalesRecord> records = new ArrayList<>();

        out.println("データ集計ツールへようこそ。コマンド: add <商品名> <カテゴリ> <金額> <数量> / list / byCategory / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(records, line, out);
            } catch (NumberFormatException e) {
                // 不正な数値入力はクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: 数値の形式が不正です");
            } catch (IllegalArgumentException e) {
                // 不正な入力形式・不明なコマンドも同様に継続可能なエラーとして扱う。
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    /**
     * 1行分のコマンドを解釈して実行する。
     * @param records 登録済みレコード一覧
     * @param line 入力行
     * @param out 出力先
     * @throws NumberFormatException 金額・数量が不正な場合
     * @throws IllegalArgumentException コマンドが不明、または引数の個数が不正な場合
     */
    private static void handleCommand(List<SalesRecord> records, String line, PrintStream out) {
        if (line.equals("list")) {
            printList(records, out);
        } else if (line.equals("byCategory")) {
            printByCategory(records, out);
        } else if (line.startsWith("add ")) {
            String[] parts = line.substring(4).trim().split("\\s+");
            if (parts.length != 4) {
                throw new IllegalArgumentException("入力形式が不正です: " + line);
            }
            records.add(new SalesRecord(parts[0], parts[1], new BigDecimal(parts[2]), Integer.parseInt(parts[3])));
        } else {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }

    private static void printList(List<SalesRecord> records, PrintStream out) {
        for (SalesRecord record : records) {
            out.println(record.product() + " " + record.category() + " " + record.amount() + " " + record.quantity());
        }
        out.println("合計金額: " + SalesAggregator.totalSales(records));
        out.println("合計数量: " + SalesAggregator.totalQuantity(records));
        out.printf("平均金額: %.2f%n", SalesAggregator.averageAmount(records));
        SalesAggregator.maxByAmount(records)
                .ifPresent(max -> out.println("最高額: " + max.product() + " " + max.amount()));
    }

    private static void printByCategory(List<SalesRecord> records, PrintStream out) {
        Map<String, BigDecimal> totals = SalesAggregator.totalByCategory(records);
        for (Map.Entry<String, BigDecimal> entry : totals.entrySet()) {
            out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
