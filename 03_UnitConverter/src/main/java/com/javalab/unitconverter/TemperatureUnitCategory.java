package com.javalab.unitconverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

/**
 * 温度カテゴリ(摂氏 c / 華氏 f / 絶対温度 k)。長さ・重さと異なり係数の掛け算/割り算だけでは
 * 変換できない(例: 摂氏→華氏は {@code value * 9/5 + 32})ため、摂氏を経由する変換式で実装する。
 */
final class TemperatureUnitCategory implements UnitCategory {

    private static final int SCALE = 10;
    private static final Set<String> UNITS = Set.of("c", "f", "k");
    private static final BigDecimal FAHRENHEIT_OFFSET = new BigDecimal("32");
    private static final BigDecimal FAHRENHEIT_RATIO = new BigDecimal("9").divide(new BigDecimal("5"), SCALE, RoundingMode.HALF_UP);
    private static final BigDecimal KELVIN_OFFSET = new BigDecimal("273.15");

    @Override
    public boolean supports(String unit) {
        return UNITS.contains(unit);
    }

    @Override
    public BigDecimal convert(BigDecimal value, String fromUnit, String toUnit) {
        BigDecimal celsius = toCelsius(value, fromUnit);
        return fromCelsius(celsius, toUnit);
    }

    private BigDecimal toCelsius(BigDecimal value, String unit) {
        return switch (unit) {
            case "c" -> value;
            case "f" -> value.subtract(FAHRENHEIT_OFFSET).divide(FAHRENHEIT_RATIO, SCALE, RoundingMode.HALF_UP);
            case "k" -> value.subtract(KELVIN_OFFSET);
            default -> throw new UnitConverterException("不明な単位です: " + unit);
        };
    }

    private BigDecimal fromCelsius(BigDecimal celsius, String unit) {
        return switch (unit) {
            case "c" -> celsius;
            case "f" -> celsius.multiply(FAHRENHEIT_RATIO).add(FAHRENHEIT_OFFSET);
            case "k" -> celsius.add(KELVIN_OFFSET);
            default -> throw new UnitConverterException("不明な単位です: " + unit);
        };
    }
}
