package com.javalab.unitconverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * 「単位→基準単位への換算係数」の掛け算/割り算だけで変換できるカテゴリ(長さ・重さなど)。
 */
final class FactorBasedUnitCategory implements UnitCategory {

    // 割り切れない変換(例: 1/3 m の cm 変換)でも例外にならないよう、スケール10で丸める。
    private static final int DIVISION_SCALE = 10;

    private final Map<String, BigDecimal> factors;

    FactorBasedUnitCategory(Map<String, BigDecimal> factors) {
        this.factors = factors;
    }

    @Override
    public boolean supports(String unit) {
        return factors.containsKey(unit);
    }

    @Override
    public BigDecimal convert(BigDecimal value, String fromUnit, String toUnit) {
        BigDecimal fromFactor = factors.get(fromUnit);
        BigDecimal toFactor = factors.get(toUnit);
        return value.multiply(fromFactor).divide(toFactor, DIVISION_SCALE, RoundingMode.HALF_UP);
    }
}
