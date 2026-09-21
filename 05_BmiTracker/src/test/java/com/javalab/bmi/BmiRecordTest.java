package com.javalab.bmi;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link BmiRecord} のCSV形式変換({@link BmiRecord#toCsvLine()} / {@link BmiRecord#fromCsvLine(String)})を検証するテスト。
 * 書き出しと読み込みが対になっていることを確認するため、両方向をそれぞれテストする。
 */
class BmiRecordTest {

    @Test
    void toCsvLineFormatsAllFieldsCommaSeparated() {
        BmiRecord record = new BmiRecord(LocalDate.of(2026, 8, 1), 170.0, 65.0, 22.49, "普通体重");

        assertEquals("2026-08-01,170.0,65.0,22.49,普通体重", record.toCsvLine());
    }

    @Test
    void fromCsvLineParsesAllFields() {
        // カンマ区切りの5フィールド(日付・身長・体重・BMI・区分)が
        // それぞれ正しい型(LocalDate/double/String)に変換されることを確認する。
        BmiRecord record = BmiRecord.fromCsvLine("2026-08-01,170.0,65.0,22.49,普通体重");

        assertEquals(LocalDate.of(2026, 8, 1), record.getDate());
        assertEquals(170.0, record.getHeightCm());
        assertEquals(65.0, record.getWeightKg());
        assertEquals(22.49, record.getBmi());
        assertEquals("普通体重", record.getCategory());
    }

    @Test
    void fromCsvLineThrowsIllegalArgumentExceptionForTooFewColumns() {
        // カラム数不足の行はArrayIndexOutOfBoundsExceptionになっていた。
        // Main.runの既存のcatch節(IllegalArgumentException)で拾えるよう変換する。
        assertThrows(IllegalArgumentException.class,
                () -> BmiRecord.fromCsvLine("2026-08-01,170.0"));
    }

    @Test
    void fromCsvLineThrowsIllegalArgumentExceptionForInvalidDate() {
        // 不正な日付はDateTimeParseException(IllegalArgumentExceptionのサブクラスではない)になっていた。
        // 同様にIllegalArgumentExceptionへ変換する。
        assertThrows(IllegalArgumentException.class,
                () -> BmiRecord.fromCsvLine("not-a-date,170.0,65.0,22.49,普通体重"));
    }
}
