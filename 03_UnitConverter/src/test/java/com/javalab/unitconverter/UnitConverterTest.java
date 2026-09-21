package com.javalab.unitconverter;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link UnitConverter} の変換ロジックを検証するテスト。
 * 同一カテゴリ内の正常な変換(長さ・重さ・温度それぞれ)と、
 * 異常系(未知の単位・カテゴリをまたぐ変換)の両方を確認する。
 */
class UnitConverterTest {

    private final UnitConverter converter = new UnitConverter();

    @Test
    void convertsKilometersToMeters() {
        // km→mは基準単位(m)への換算係数が1000なので、5km = 5000mになる。
        assertEquals(0, new BigDecimal("5000").compareTo(
                converter.convert(new BigDecimal("5"), "km", "m")));
    }

    @Test
    void convertsCentimetersToMeters() {
        // cm→mは換算係数が0.01なので、100cm = 1mになる(1未満の係数も正しく扱えることを確認)。
        assertEquals(0, BigDecimal.ONE.compareTo(
                converter.convert(new BigDecimal("100"), "cm", "m")));
    }

    @Test
    void convertsKilogramsToGrams() {
        // 重さカテゴリでも長さと同様の変換ロジックが機能することを確認する。
        assertEquals(0, new BigDecimal("1000").compareTo(
                converter.convert(BigDecimal.ONE, "kg", "g")));
    }

    @Test
    void convertsMilligramsToGrams() {
        assertEquals(0, BigDecimal.ONE.compareTo(
                converter.convert(new BigDecimal("1000"), "mg", "g")));
    }

    @Test
    void sameUnitConversionReturnsExactInputWithoutFloatingPointError() {
        // doubleでは 7 * 0.01 / 0.01 が 7.000000000000001 のような誤差になっていた。
        // BigDecimalによる厳密計算では同一単位の変換は入力値そのものを返すことを確認する。
        assertEquals(0, new BigDecimal("7").compareTo(
                converter.convert(new BigDecimal("7"), "cm", "cm")));
    }

    @Test
    void convertsCentimetersToMillimetersWithoutFloatingPointError() {
        // doubleでは 123.456 * 0.01 / 0.001 が 1234.5600000000002 のような誤差になっていた。
        assertEquals(0, new BigDecimal("1234.56").compareTo(
                converter.convert(new BigDecimal("123.456"), "cm", "mm")));
    }

    @Test
    void throwsExceptionForUnknownUnit() {
        // "xyz"はどのカテゴリにも存在しない単位。UnitConverterExceptionをスローすることを確認する。
        assertThrows(UnitConverterException.class,
                () -> converter.convert(BigDecimal.ONE, "xyz", "m"));
    }

    @Test
    void throwsExceptionForCrossCategoryConversion() {
        // "km"(長さ)と"g"(重さ)は別カテゴリのため、変換不可としてUnitConverterExceptionになることを確認する。
        assertThrows(UnitConverterException.class,
                () -> converter.convert(BigDecimal.ONE, "km", "g"));
    }

    @Test
    void unitLookupIsCaseInsensitive() {
        // "KM"のような大文字表記でも小文字の"km"と同じカテゴリ・係数として扱われることを確認する。
        assertEquals(0, new BigDecimal("5000").compareTo(
                converter.convert(new BigDecimal("5"), "KM", "M")));
    }

    @Test
    void convertsCelsiusToFahrenheit() {
        // 温度は係数の掛け算/割り算では変換できないため、UnitCategoryの抽象化で公式ベースの
        // 変換(摂氏0度 = 華氏32度)ができることを確認する。
        assertEquals(0, new BigDecimal("32").compareTo(
                converter.convert(BigDecimal.ZERO, "c", "f")));
    }

    @Test
    void convertsFahrenheitToCelsius() {
        // 華氏212度 = 摂氏100度(水の沸点)。
        assertEquals(0, new BigDecimal("100").compareTo(
                converter.convert(new BigDecimal("212"), "f", "c")));
    }

    @Test
    void convertsCelsiusToKelvin() {
        assertEquals(0, new BigDecimal("273.15").compareTo(
                converter.convert(BigDecimal.ZERO, "c", "k")));
    }

    @Test
    void throwsExceptionForCrossCategoryConversionBetweenTemperatureAndLength() {
        // 温度("c")と長さ("m")は別カテゴリのため変換できないことを確認する。
        assertThrows(UnitConverterException.class,
                () -> converter.convert(BigDecimal.ZERO, "c", "m"));
    }
}
