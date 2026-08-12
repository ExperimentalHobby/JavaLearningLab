package com.javalab.rpn;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;

/**
 * 対話式CLI RPN電卓のエントリーポイント。
 * 標準入力から逆ポーランド記法の数式を1行ずつ読み取り、評価結果を表示する。
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
        out.println("RPN電卓へようこそ。逆ポーランド記法の式を入力してください(例: 3 4 +)。終了: exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                BigDecimal result = RpnCalculator.evaluate(line);
                out.println("= " + result);
            } catch (NumberFormatException e) {
                out.println("エラー: 数値の形式が不正です");
            } catch (RpnCalculatorException | ArithmeticException e) {
                // 不正な式・オペランド不足・ゼロ除算はクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: " + e.getMessage());
            }
        }
    }
}
