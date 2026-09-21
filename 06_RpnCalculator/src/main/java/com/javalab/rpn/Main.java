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
        out.println("RPN電卓へようこそ。逆ポーランド記法の式を入力してください(例: 3 4 + / 2 10 ^)。終了: exit");

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
                out.println("= " + format(result));
            } catch (RpnCalculatorException | ArithmeticException e) {
                // 不正な式・オペランド不足・ゼロ除算はクラッシュさせず、エラー表示して次の入力へ進む。
                // (数値の形式不正はRpnCalculator.parseOperand()が既にRpnCalculatorExceptionへ変換済み)
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    /**
     * 表示用に整形する。{@link RpnCalculator#evaluate(String)}は除算をスケール10で丸めるため、
     * 割り切れるケースでも{@code "5.0000000000"}のような表示になり得る。末尾の余分なゼロを取り除く。
     * @param value 整形対象の値
     * @return 整形後の文字列
     */
    private static String format(BigDecimal value) {
        // BigDecimal.ZERO.stripTrailingZeros() は "0E-10" のような表記になり得るため、0は個別に扱う。
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }
}
