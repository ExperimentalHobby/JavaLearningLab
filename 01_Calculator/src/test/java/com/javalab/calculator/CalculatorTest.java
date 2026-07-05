package com.javalab.calculator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void addsTwoDecimalValues() {
        BigDecimal result = calculator.add(new BigDecimal("0.1"), new BigDecimal("0.2"));

        assertEquals(0, new BigDecimal("0.3").compareTo(result));
    }

    @Test
    void subtractsTwoDecimalValuesWithoutFloatingPointError() {
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
        BigDecimal result = calculator.divide(new BigDecimal("10"), new BigDecimal("4"));

        assertEquals(0, new BigDecimal("2.5").compareTo(result));
    }

    @Test
    void dividesNonTerminatingDecimalWithoutThrowing() {
        BigDecimal result = calculator.divide(new BigDecimal("1"), new BigDecimal("3"));

        assertEquals(0, new BigDecimal("0.3333333333").compareTo(result));
    }

    @Test
    void divisionByZeroThrowsArithmeticException() {
        assertThrows(ArithmeticException.class,
                () -> calculator.divide(new BigDecimal("1"), BigDecimal.ZERO));
    }

    @Test
    void calculateDispatchesToCorrectOperationByOperatorSymbol() {
        BigDecimal result = calculator.calculate(new BigDecimal("0.6"), "-", new BigDecimal("0.2"));

        assertEquals(0, new BigDecimal("0.4").compareTo(result));
    }

    @Test
    void calculateThrowsCalculatorExceptionForUnknownOperator() {
        assertThrows(CalculatorException.class,
                () -> calculator.calculate(new BigDecimal("1"), "^", new BigDecimal("2")));
    }
}
