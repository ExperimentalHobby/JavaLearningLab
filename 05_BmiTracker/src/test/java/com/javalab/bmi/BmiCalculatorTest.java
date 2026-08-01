package com.javalab.bmi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BmiCalculatorTest {

    @Test
    void calculatesBmiFromHeightAndWeight() {
        assertEquals(22.49, BmiCalculator.calculate(170, 65), 0.001);
    }

    @Test
    void classifiesUnderweight() {
        assertEquals("低体重", BmiCalculator.classify(17.0));
    }

    @Test
    void classifiesNormalWeight() {
        assertEquals("普通体重", BmiCalculator.classify(22.0));
    }

    @Test
    void classifiesObesityLevel1() {
        assertEquals("肥満(1度)", BmiCalculator.classify(27.0));
    }

    @Test
    void classifiesObesityLevel2OrAbove() {
        assertEquals("肥満(2度以上)", BmiCalculator.classify(31.0));
    }
}
