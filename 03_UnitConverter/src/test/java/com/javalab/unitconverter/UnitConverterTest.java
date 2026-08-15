package com.javalab.unitconverter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link UnitConverter} の変換ロジックを検証するテスト。
 * 同一カテゴリ内の正常な変換(長さ・重さそれぞれ)と、
 * 異常系(未知の単位・カテゴリをまたぐ変換)の両方を確認する。
 */
class UnitConverterTest {

    private final UnitConverter converter = new UnitConverter();

    @Test
    void convertsKilometersToMeters() {
        // km→mは基準単位(m)への換算係数が1000なので、5km = 5000mになる。
        assertEquals(5000, converter.convert(5, "km", "m"));
    }

    @Test
    void convertsCentimetersToMeters() {
        // cm→mは換算係数が0.01なので、100cm = 1mになる(1未満の係数も正しく扱えることを確認)。
        assertEquals(1, converter.convert(100, "cm", "m"));
    }

    @Test
    void convertsKilogramsToGrams() {
        // 重さカテゴリでも長さと同様の変換ロジックが機能することを確認する。
        assertEquals(1000, converter.convert(1, "kg", "g"));
    }

    @Test
    void convertsMilligramsToGrams() {
        assertEquals(1, converter.convert(1000, "mg", "g"));
    }

    @Test
    void throwsExceptionForUnknownUnit() {
        // "xyz"はどのカテゴリのMapにも存在しない単位。findTableContaining()が
        // 該当カテゴリを見つけられずUnitConverterExceptionをスローすることを確認する。
        assertThrows(UnitConverterException.class,
                () -> converter.convert(1, "xyz", "m"));
    }

    @Test
    void throwsExceptionForCrossCategoryConversion() {
        // "km"(長さ)と"g"(重さ)は別カテゴリのため、fromUnitは見つかってもtoUnitが
        // 同じテーブルに存在せず、変換不可としてUnitConverterExceptionになることを確認する。
        assertThrows(UnitConverterException.class,
                () -> converter.convert(1, "km", "g"));
    }
}
