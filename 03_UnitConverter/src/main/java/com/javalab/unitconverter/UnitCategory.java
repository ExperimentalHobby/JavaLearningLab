package com.javalab.unitconverter;

import java.math.BigDecimal;

/**
 * 単位変換の「カテゴリ」を表す抽象。長さ・重さのような係数の掛け算/割り算で変換できるカテゴリと、
 * 温度のように公式(摂氏を経由した変換式)でしか変換できないカテゴリを、同じ窓口で扱えるようにする。
 */
public interface UnitCategory {

    /**
     * このカテゴリが指定された単位を扱えるかを返す。
     * @param unit 単位(小文字に正規化済み)
     * @return 扱える場合true
     */
    boolean supports(String unit);

    /**
     * 数値を変換元単位から変換先単位に変換する。
     * @param value 変換対象の数値
     * @param fromUnit 変換元単位(小文字に正規化済み、{@link #supports(String)} がtrueであること)
     * @param toUnit 変換先単位(小文字に正規化済み、{@link #supports(String)} がtrueであること)
     * @return 変換後の数値
     */
    BigDecimal convert(BigDecimal value, String fromUnit, String toUnit);
}
