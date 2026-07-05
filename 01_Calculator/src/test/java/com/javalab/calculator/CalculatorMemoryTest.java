package com.javalab.calculator;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorMemoryTest {

    private final Calculator calculator = new Calculator();

    @Test
    void initialMemoryIsZero() {
        assertEquals(0, BigDecimal.ZERO.compareTo(calculator.memoryRecall()));
    }

    @Test
    void memoryAddAddsValueToMemory() {
        calculator.memoryAdd(new BigDecimal("5"));

        assertEquals(0, new BigDecimal("5").compareTo(calculator.memoryRecall()));
    }

    @Test
    void memorySubtractSubtractsValueFromMemory() {
        calculator.memoryAdd(new BigDecimal("5"));
        calculator.memorySubtract(new BigDecimal("2"));

        assertEquals(0, new BigDecimal("3").compareTo(calculator.memoryRecall()));
    }

    @Test
    void memoryClearResetsMemoryToZero() {
        calculator.memoryAdd(new BigDecimal("5"));
        calculator.memoryClear();

        assertEquals(0, BigDecimal.ZERO.compareTo(calculator.memoryRecall()));
    }
}
