package com.javalab.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 四則演算とメモリ機能(M+, M-, MR, MC)を提供する電卓ロジック。
 * 数値表現に {@code double} ではなく {@link BigDecimal} を用いることで、
 * {@code 0.6 - 0.2} のような小数演算が丸め誤差なく厳密に計算されるようにしている。
 */
public class Calculator {

    // 割り切れない除算(例: 1/3)でも例外にならないよう、スケール10で丸める。
    private static final int DIVISION_SCALE = 10;

    /** メモリレジスタ。M+/M-/MR/MC の対象となる内部状態。 */
    private BigDecimal memory = BigDecimal.ZERO;

    /**
     * 加算する。
     * @param a 被加数
     * @param b 加数
     * @return a + b
     */
    public BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    /**
     * 減算する。
     * @param a 被減数
     * @param b 減数
     * @return a - b
     */
    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    /**
     * 乗算する。
     * @param a 被乗数
     * @param b 乗数
     * @return a * b
     */
    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    /**
     * 除算する。
     * ゼロ除算は {@link BigDecimal#divide(BigDecimal, int, RoundingMode)} が自動的に
     * {@link ArithmeticException} をスローする。
     * @param a 被除数
     * @param b 除数
     * @return a / b(スケール10・{@link RoundingMode#HALF_UP} で丸めた値)
     * @throws ArithmeticException b がゼロの場合
     */
    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        return a.divide(b, DIVISION_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 演算子文字列に応じて四則演算を実行する。
     * @param a 左辺値
     * @param operator 演算子("+", "-", "*", "/" のいずれか)
     * @param b 右辺値
     * @return 計算結果
     * @throws CalculatorException operator が上記いずれにも一致しない場合
     */
    public BigDecimal calculate(BigDecimal a, String operator, BigDecimal b) {
        return switch (operator) {
            case "+" -> add(a, b);
            case "-" -> subtract(a, b);
            case "*" -> multiply(a, b);
            case "/" -> divide(a, b);
            default -> throw new CalculatorException("不正な演算子です: " + operator);
        };
    }

    /**
     * 現在のメモリ値を返す(MR)。
     * @return メモリレジスタの値
     */
    public BigDecimal memoryRecall() {
        return memory;
    }

    /**
     * メモリに値を加算する(M+)。
     * @param value 加算する値
     */
    public void memoryAdd(BigDecimal value) {
        memory = memory.add(value);
    }

    /**
     * メモリから値を減算する(M-)。
     * @param value 減算する値
     */
    public void memorySubtract(BigDecimal value) {
        memory = memory.subtract(value);
    }

    /**
     * メモリを 0 にリセットする(MC)。
     */
    public void memoryClear() {
        memory = BigDecimal.ZERO;
    }
}
