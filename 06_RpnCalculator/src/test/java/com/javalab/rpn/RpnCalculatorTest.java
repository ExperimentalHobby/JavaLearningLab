package com.javalab.rpn;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link RpnCalculator#evaluate(String)} のスタックベース評価ロジックを検証するテスト。
 * 四則演算・複数演算子の組み合わせに加え、スタックの状態が不正になる異常系
 * (オペランド不足/過多、不正トークン、ゼロ除算)を網羅する。
 */
class RpnCalculatorTest {

    @Test
    void addsTwoOperands() {
        BigDecimal result = RpnCalculator.evaluate("3 4 +");

        assertEquals(0, new BigDecimal("7").compareTo(result));
    }

    @Test
    void subtractsTwoOperands() {
        BigDecimal result = RpnCalculator.evaluate("10 2 -");

        assertEquals(0, new BigDecimal("8").compareTo(result));
    }

    @Test
    void multipliesTwoOperands() {
        BigDecimal result = RpnCalculator.evaluate("6 7 *");

        assertEquals(0, new BigDecimal("42").compareTo(result));
    }

    @Test
    void dividesTwoOperands() {
        BigDecimal result = RpnCalculator.evaluate("20 4 /");

        assertEquals(0, new BigDecimal("5").compareTo(result));
    }

    @Test
    void evaluatesExpressionWithMultipleOperators() {
        // "2 3 4 + *" は (3+4)*2 = 14 と同じ意味になる。
        // 2をpush→3をpush→4をpush→"+"で3,4をpopし7をpush→"*"で2,7をpopし14をpush、という流れ。
        BigDecimal result = RpnCalculator.evaluate("2 3 4 + *");

        assertEquals(0, new BigDecimal("14").compareTo(result));
    }

    @Test
    void divisionByZeroThrowsArithmeticException() {
        assertThrows(ArithmeticException.class, () -> RpnCalculator.evaluate("1 0 /"));
    }

    @Test
    void insufficientOperandsThrowsRpnCalculatorException() {
        // "1 +" は演算子"+"に対してオペランドが1つしか無い(popが2回目で失敗する)不正な式。
        assertThrows(RpnCalculatorException.class, () -> RpnCalculator.evaluate("1 +"));
    }

    @Test
    void tooManyOperandsThrowsRpnCalculatorException() {
        // "3 4 5" は演算子が無いため評価後もスタックに3つ残ってしまい、
        // 「最終的にスタックが1つだけ残る」という正しいRPN式の条件を満たさない。
        assertThrows(RpnCalculatorException.class, () -> RpnCalculator.evaluate("3 4 5"));
    }

    @Test
    void invalidTokenThrowsRpnCalculatorException() {
        // "x" は数値でも演算子でもないため、parseOperand()でのBigDecimal変換に失敗する。
        assertThrows(RpnCalculatorException.class, () -> RpnCalculator.evaluate("3 x +"));
    }
}
