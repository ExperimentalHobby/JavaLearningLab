package com.javalab.unitconverter;

import java.util.List;
import java.util.Map;

/**
 * 単位変換ロジック。カテゴリ(長さ・重さ)ごとに「単位→基準単位への換算係数」をHashMapで管理し、
 * 同じカテゴリ内の単位同士のみ変換を許可する。
 */
public class UnitConverter {

    // 長さカテゴリ: 基準単位はメートル。
    private final Map<String, Double> lengthFactors = Map.of(
            "m", 1.0,
            "km", 1000.0,
            "cm", 0.01,
            "mm", 0.001
    );

    // 重さカテゴリ: 基準単位はグラム。
    private final Map<String, Double> weightFactors = Map.of(
            "g", 1.0,
            "kg", 1000.0,
            "mg", 0.001
    );

    private final List<Map<String, Double>> categories = List.of(lengthFactors, weightFactors);

    /**
     * 数値を変換元単位から変換先単位に変換する。
     * @param value 変換対象の数値
     * @param fromUnit 変換元単位
     * @param toUnit 変換先単位
     * @return 変換後の数値
     * @throws UnitConverterException fromUnitが未知の単位、またはtoUnitがfromUnitと異なるカテゴリの場合
     */
    public double convert(double value, String fromUnit, String toUnit) {
        Map<String, Double> table = findTableContaining(fromUnit);
        if (!table.containsKey(toUnit)) {
            throw new UnitConverterException(
                    "異なるカテゴリの単位には変換できません: " + fromUnit + " -> " + toUnit);
        }
        double fromFactor = table.get(fromUnit);
        double toFactor = table.get(toUnit);
        return value * fromFactor / toFactor;
    }

    private Map<String, Double> findTableContaining(String unit) {
        return categories.stream()
                .filter(table -> table.containsKey(unit))
                .findFirst()
                .orElseThrow(() -> new UnitConverterException("不明な単位です: " + unit));
    }
}
