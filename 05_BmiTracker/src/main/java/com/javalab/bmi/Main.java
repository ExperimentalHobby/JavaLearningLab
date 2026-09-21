package com.javalab.bmi;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * 対話式CLI BMIトラッカーのエントリーポイント。
 * 標準入力からコマンド({@code add}/{@code list}/{@code save}/{@code load})を1行ずつ読み取り、
 * 結果を標準出力に表示する。{@code exit} で終了する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out, new File("bmi.csv"), LocalDate::now);
    }

    /**
     * REPLループ本体。{@link Scanner}/{@link PrintStream}/保存先ファイルに加え、
     * 測定日を決定する処理も {@link Supplier} として受け取ることで、
     * テストから固定の日付を注入できるようにしている。
     * @param scanner 入力読み取り元
     * @param out 出力先
     * @param file save/loadコマンドの対象ファイル
     * @param dateSupplier addコマンドで使用する測定日を決定する処理(本番では現在日、テストでは固定値)
     */
    static void run(Scanner scanner, PrintStream out, File file, Supplier<LocalDate> dateSupplier) {
        BmiHistory history = new BmiHistory();

        out.println("BMIトラッカーへようこそ。コマンド: add <身長cm> <体重kg> / list / list <開始日> <終了日> / stats / save / load / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(history, line, out, file, dateSupplier);
            } catch (NumberFormatException e) {
                // 不正な数値入力(例: "abc 65")はクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: 数値の形式が不正です");
            } catch (IllegalArgumentException e) {
                // 不明なコマンド・引数の個数不一致も同様に継続可能なエラーとして扱う。
                out.println("エラー: " + e.getMessage());
            } catch (IOException e) {
                out.println("エラー: ファイル操作に失敗しました: " + e.getMessage());
            }
        }
    }

    /**
     * 1行分のコマンドを解釈して実行する。
     * @param history 操作対象のBMI履歴
     * @param line 入力行
     * @param out 出力先
     * @param file save/loadコマンドの対象ファイル
     * @param dateSupplier addコマンドで使用する測定日を決定する処理
     * @throws IOException save/load時のファイル操作に失敗した場合
     * @throws NumberFormatException addコマンドの数値が不正な場合
     * @throws IllegalArgumentException コマンドが不明、または引数の個数が不正な場合
     */
    private static void handleCommand(
            BmiHistory history, String line, PrintStream out, File file, Supplier<LocalDate> dateSupplier)
            throws IOException {
        if (line.equals("list")) {
            printRecords(history.getRecords(), out);
        } else if (line.startsWith("list ")) {
            String[] range = line.substring(5).split("\\s+");
            if (range.length != 2) {
                throw new IllegalArgumentException("入力形式が不正です: " + line);
            }
            try {
                LocalDate from = LocalDate.parse(range[0]);
                LocalDate to = LocalDate.parse(range[1]);
                printRecords(history.getRecords(from, to), out);
            } catch (DateTimeParseException e) {
                // DateTimeParseExceptionはIllegalArgumentExceptionのサブクラスではないため、
                // Main.runの既存のcatch節で拾えるよう変換する(BmiRecord.fromCsvLineと同じ方針)。
                throw new IllegalArgumentException("日付の形式が不正です: " + line, e);
            }
        } else if (line.equals("stats")) {
            printStats(history, out);
        } else if (line.equals("save")) {
            history.saveTo(file);
            out.println("保存しました");
        } else if (line.equals("load")) {
            history.loadFrom(file);
            out.println("読み込みました");
        } else if (line.startsWith("add ")) {
            String[] parts = line.substring(4).split("\\s+");
            if (parts.length != 2) {
                throw new IllegalArgumentException("入力形式が不正です: " + line);
            }
            double heightCm = Double.parseDouble(parts[0]);
            double weightKg = Double.parseDouble(parts[1]);
            history.add(dateSupplier.get(), heightCm, weightKg);
        } else {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }

    /**
     * 記録一覧を表示する。各行に前回(直前)の記録とのBMI差(前回比)を付記する
     * (先頭の記録は比較対象がないため「初回」と表示する)。
     * @param records 表示対象の記録一覧
     * @param out 出力先
     */
    private static void printRecords(List<BmiRecord> records, PrintStream out) {
        if (records.isEmpty()) {
            out.println("該当する記録がありません");
            return;
        }
        BmiRecord previous = null;
        for (BmiRecord record : records) {
            String diff = previous == null
                    ? "初回"
                    : "前回比: " + formatDiff(record.getBmi() - previous.getBmi());
            out.println(record.toCsvLine() + " (" + diff + ")");
            previous = record;
        }
    }

    private static String formatDiff(double diff) {
        return diff >= 0 ? String.format("+%.2f", diff) : String.format("%.2f", diff);
    }

    /**
     * 平均・最大・最小BMIを表示する。履歴が1件もない場合はその旨を表示する。
     * @param history 集計対象の履歴
     * @param out 出力先
     */
    private static void printStats(BmiHistory history, PrintStream out) {
        if (history.getRecords().isEmpty()) {
            out.println("履歴がありません");
            return;
        }
        out.printf("平均BMI: %.2f%n", history.averageBmi());
        history.maxRecord().ifPresent(record -> out.printf("最大BMI: %.2f (%s)%n", record.getBmi(), record.getDate()));
        history.minRecord().ifPresent(record -> out.printf("最小BMI: %.2f (%s)%n", record.getBmi(), record.getDate()));
    }
}
