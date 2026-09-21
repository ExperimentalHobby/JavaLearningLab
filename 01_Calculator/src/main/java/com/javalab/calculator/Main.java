package com.javalab.calculator;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 対話式CLI電卓のエントリーポイント。
 * 標準入力から式(例: {@code 3 + 5})またはメモリコマンド({@code M+}/{@code M-}/{@code MR}/{@code MC})を
 * 1行ずつ読み取り、結果を標準出力に表示する。{@code exit} で終了する。
 */
public class Main {

    // "3+5"のような空白なし入力にも対応するため、空白の有無に依存せず数値・演算子を切り出す。
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile(
            "^(?<left>[+-]?\\d+(?:\\.\\d+)?)\\s*(?<operator>[+\\-*/])\\s*(?<right>[+-]?\\d+(?:\\.\\d+)?)$");

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            run(scanner, System.out);
        }
    }

    /**
     * REPLループ本体。{@link Scanner}/{@link PrintStream} を引数として受け取ることで、
     * テストから {@code StringReader}/{@code ByteArrayOutputStream} を注入できるようにしている。
     * @param scanner 入力読み取り元
     * @param out 出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        Calculator calculator = new Calculator();
        // 直前の計算結果。M+/M-/MR がこの値を対象にメモリを更新・参照する。
        BigDecimal lastResult = BigDecimal.ZERO;

        out.println("電卓アプリへようこそ。'数値 演算子 数値'の形式で入力してください(例: 3 + 5)。");
        out.println("メモリ操作: M+ / M- / MR / MC、終了: exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                // "exit"がequalsIgnoreCaseで大小文字を無視しているのと同様に、メモリコマンドも
                // 大文字化して判定することで"m+"のような小文字入力も受け付ける。
                lastResult = switch (line.toUpperCase(Locale.ROOT)) {
                    case "M+" -> {
                        calculator.memoryAdd(lastResult);
                        out.println("メモリ: " + format(calculator.memoryRecall()));
                        // メモリ操作はlastResultを変更しないため、そのまま維持する。
                        yield lastResult;
                    }
                    case "M-" -> {
                        calculator.memorySubtract(lastResult);
                        out.println("メモリ: " + format(calculator.memoryRecall()));
                        yield lastResult;
                    }
                    case "MR" -> {
                        // MRはメモリ値を呼び出した結果として扱い、以降の計算にも使えるようlastResultを更新する。
                        BigDecimal recalled = calculator.memoryRecall();
                        out.println("メモリ: " + format(recalled));
                        yield recalled;
                    }
                    case "MC" -> {
                        calculator.memoryClear();
                        out.println("メモリをクリアしました");
                        yield lastResult;
                    }
                    default -> evaluateExpression(calculator, line, out);
                };
            } catch (NumberFormatException e) {
                // 不正な数値入力(例: "abc - 3")はクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: 数値の形式が不正です");
            } catch (CalculatorException e) {
                // 不正な演算子・ゼロ除算も同様に継続可能なエラーとして扱う。
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    /**
     * {@code "数値 演算子 数値"} 形式の1行を解析して計算する。空白の有無・位置によらず
     * {@code "3+5"} {@code "3 + 5"} のいずれも受け付ける。
     * @param calculator 演算ロジック
     * @param line 入力行
     * @param out 結果の出力先
     * @return 計算結果
     * @throws CalculatorException 数値・演算子として解析できない形式の場合
     */
    private static BigDecimal evaluateExpression(Calculator calculator, String line, PrintStream out) {
        Matcher matcher = EXPRESSION_PATTERN.matcher(line);
        if (!matcher.matches()) {
            throw new CalculatorException("入力形式が不正です: " + line);
        }
        BigDecimal a = new BigDecimal(matcher.group("left"));
        String operator = matcher.group("operator");
        BigDecimal b = new BigDecimal(matcher.group("right"));

        BigDecimal result = calculator.calculate(a, operator, b);
        out.println("= " + format(result));
        return result;
    }

    /**
     * 表示用に整形する。末尾の余分なゼロを取り除く(例: {@code 0.4000000000} → {@code 0.4})。
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
