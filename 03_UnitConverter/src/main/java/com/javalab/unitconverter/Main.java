package com.javalab.unitconverter;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * 対話式CLI単位変換ツールのエントリーポイント。
 * 標準入力から{@code "数値 変換元単位 変換先単位"}を1行ずつ読み取り、変換結果を表示する。
 * {@code exit} で終了する。
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
        UnitConverter converter = new UnitConverter();

        out.println("単位変換ツールへようこそ。'数値 変換元単位 変換先単位'の形式で入力してください(例: 5 km m)。");
        out.println("対応単位: 長さ(m, km, cm, mm) / 重さ(g, kg, mg)、終了: exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                String[] parts = line.split("\\s+");
                if (parts.length != 3) {
                    throw new UnitConverterException("入力形式が不正です: " + line);
                }
                double value = Double.parseDouble(parts[0]);
                double result = converter.convert(value, parts[1], parts[2]);
                out.println("= " + result + " " + parts[2]);
            } catch (NumberFormatException e) {
                // 不正な数値入力(例: "abc km m")はクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: 数値の形式が不正です");
            } catch (UnitConverterException e) {
                // 不明な単位・カテゴリ不一致も同様に継続可能なエラーとして扱う。
                out.println("エラー: " + e.getMessage());
            }
        }
    }
}
