package com.javalab.rpn;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        BigDecimal result = RpnCalculator.evaluate("2 3 4 + *");

        assertEquals(0, new BigDecimal("14").compareTo(result));
    }

    @Test
    void divisionByZeroThrowsArithmeticException() {
        assertThrows(ArithmeticException.class, () -> RpnCalculator.evaluate("1 0 /"));
    }

    @Test
    void insufficientOperandsThrowsRpnCalculatorException() {
        assertThrows(RpnCalculatorException.class, () -> RpnCalculator.evaluate("1 +"));
    }

    @Test
    void tooManyOperandsThrowsRpnCalculatorException() {
        assertThrows(RpnCalculatorException.class, () -> RpnCalculator.evaluate("3 4 5"));
    }

    @Test
    void invalidTokenThrowsRpnCalculatorException() {
        assertThrows(RpnCalculatorException.class, () -> RpnCalculator.evaluate("3 x +"));
    }
}
