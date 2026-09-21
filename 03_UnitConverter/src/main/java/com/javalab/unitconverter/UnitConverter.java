package com.javalab.unitconverter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 単位変換ロジック。カテゴリ(長さ・重さ・温度)ごとに {@link UnitCategory} を持ち、
 * 同じカテゴリ内の単位同士のみ変換を許可する。数値表現には {@code double} ではなく
 * {@link BigDecimal} を用いることで、丸め誤差なく厳密に計算する({@code 01_Calculator} と同じ方針)。
 */
public class UnitConverter {

    // 長さカテゴリ: 基準単位はメートル。
    private static final Map<String, BigDecimal> LENGTH_FACTORS = Map.of(
            "m", BigDecimal.ONE,
            "km", new BigDecimal("1000"),
            "cm", new BigDecimal("0.01"),
            "mm", new BigDecimal("0.001")
    );

    // 重さカテゴリ: 基準単位はグラム。
    private static final Map<String, BigDecimal> WEIGHT_FACTORS = Map.of(
            "g", BigDecimal.ONE,
            "kg", new BigDecimal("1000"),
            "mg", new BigDecimal("0.001")
    );

    private final List<UnitCategory> categories = List.of(
            new FactorBasedUnitCategory(LENGTH_FACTORS),
            new FactorBasedUnitCategory(WEIGHT_FACTORS),
            new TemperatureUnitCategory()
    );

    /**
     * 数値を変換元単位から変換先単位に変換する。単位の大文字・小文字は区別しない。
     * @param value 変換対象の数値
     * @param fromUnit 変換元単位
     * @param toUnit 変換先単位
     * @return 変換後の数値
     * @throws UnitConverterException fromUnitが未知の単位、またはtoUnitがfromUnitと異なるカテゴリの場合
     */
    public BigDecimal convert(BigDecimal value, String fromUnit, String toUnit) {
        String normalizedFrom = fromUnit.toLowerCase(Locale.ROOT);
        String normalizedTo = toUnit.toLowerCase(Locale.ROOT);
        UnitCategory category = findCategorySupporting(normalizedFrom);
        if (!category.supports(normalizedTo)) {
            throw new UnitConverterException(
                    "異なるカテゴリの単位には変換できません: " + fromUnit + " -> " + toUnit);
        }
        return category.convert(value, normalizedFrom, normalizedTo);
    }

    private UnitCategory findCategorySupporting(String unit) {
        return categories.stream()
                .filter(category -> category.supports(unit))
                .findFirst()
                .orElseThrow(() -> new UnitConverterException("不明な単位です: " + unit));
    }
}
