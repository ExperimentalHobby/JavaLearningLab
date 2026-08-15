package com.javalab.calculator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Calculator} の四則演算ロジックを検証するテスト。
 * BigDecimalによる厳密な小数計算・演算子ディスパッチ・異常系(不正演算子/ゼロ除算)を確認する。
 */
class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void addsTwoDecimalValues() {
        BigDecimal result = calculator.add(new BigDecimal("0.1"), new BigDecimal("0.2"));

        // BigDecimalの値比較はequals()ではなくcompareTo()を使う。
        // equals()はスケール(小数点以下の桁数)まで一致しないとfalseになるため、
        // "0.30"のような結果を返す実装に変わっても壊れないようにしている。
        assertEquals(0, new BigDecimal("0.3").compareTo(result));
    }

    @Test
    void subtractsTwoDecimalValuesWithoutFloatingPointError() {
        // 0.6 - 0.2 はdoubleで計算すると0.39999999999999997のような誤差が出る有名な例。
        // BigDecimalを使うことで、この誤差が発生しないことを確認する。
        BigDecimal result = calculator.subtract(new BigDecimal("0.6"), new BigDecimal("0.2"));

        assertEquals(0, new BigDecimal("0.4").compareTo(result));
    }

    @Test
    void multipliesTwoDecimalValues() {
        BigDecimal result = calculator.multiply(new BigDecimal("2.5"), new BigDecimal("4"));

        assertEquals(0, new BigDecimal("10").compareTo(result));
    }

    @Test
    void dividesTwoDecimalValues() {
        // 10 / 4 = 2.5 のように割り切れるケース。
        BigDecimal result = calculator.divide(new BigDecimal("10"), new BigDecimal("4"));

        assertEquals(0, new BigDecimal("2.5").compareTo(result));
    }

    @Test
    void dividesNonTerminatingDecimalWithoutThrowing() {
        // 1 / 3 は割り切れない(循環小数)ため、スケールを指定しないdivide()では
        // ArithmeticExceptionになる。Calculator.divide()はスケール10・HALF_UPで丸めるため
        // 例外にならず計算できることを確認する。
        BigDecimal result = calculator.divide(new BigDecimal("1"), new BigDecimal("3"));

        assertEquals(0, new BigDecimal("0.3333333333").compareTo(result));
    }

    @Test
    void divisionByZeroThrowsArithmeticException() {
        // ゼロ除算はCalculator側で特別扱いしておらず、BigDecimal#divide()が
        // 自動的にスローする例外がそのまま伝播することを確認する。
        assertThrows(ArithmeticException.class,
                () -> calculator.divide(new BigDecimal("1"), BigDecimal.ZERO));
    }

    @Test
    void calculateDispatchesToCorrectOperationByOperatorSymbol() {
        // calculate()が演算子文字列("-")を見て正しくsubtract()に振り分けることを確認する。
        BigDecimal result = calculator.calculate(new BigDecimal("0.6"), "-", new BigDecimal("0.2"));

        assertEquals(0, new BigDecimal("0.4").compareTo(result));
    }

    @Test
    void calculateThrowsCalculatorExceptionForUnknownOperator() {
        // "^"のような未対応の演算子が渡された場合、calculate()のswitch文のdefault節から
        // CalculatorExceptionがスローされることを確認する。
        assertThrows(CalculatorException.class,
                () -> calculator.calculate(new BigDecimal("1"), "^", new BigDecimal("2")));
    }
}
