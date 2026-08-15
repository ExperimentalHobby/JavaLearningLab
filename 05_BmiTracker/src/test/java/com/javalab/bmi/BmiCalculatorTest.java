package com.javalab.bmi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link BmiCalculator} のBMI計算と、日本肥満学会基準による4区分判定を検証するテスト。
 * 判定は境界値のすぐ内側の値(17.0, 22.0, 27.0, 31.0)を使い、各区分に正しく分類されることを確認する。
 */
class BmiCalculatorTest {

    @Test
    void calculatesBmiFromHeightAndWeight() {
        // 身長170cm・体重65kgのBMI = 65 / 1.7^2 ≒ 22.49(小数第2位で丸め)。
        assertEquals(22.49, BmiCalculator.calculate(170, 65), 0.001);
    }

    @Test
    void classifiesUnderweight() {
        // 18.5未満は「低体重」区分。
        assertEquals("低体重", BmiCalculator.classify(17.0));
    }

    @Test
    void classifiesNormalWeight() {
        // 18.5以上25.0未満は「普通体重」区分。
        assertEquals("普通体重", BmiCalculator.classify(22.0));
    }

    @Test
    void classifiesObesityLevel1() {
        // 25.0以上30.0未満は「肥満(1度)」区分。
        assertEquals("肥満(1度)", BmiCalculator.classify(27.0));
    }

    @Test
    void classifiesObesityLevel2OrAbove() {
        // 30.0以上は「肥満(2度以上)」区分。
        assertEquals("肥満(2度以上)", BmiCalculator.classify(31.0));
    }
}
