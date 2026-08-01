package com.javalab.bmi;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BmiRecordTest {

    @Test
    void toCsvLineFormatsAllFieldsCommaSeparated() {
        BmiRecord record = new BmiRecord(LocalDate.of(2026, 8, 1), 170.0, 65.0, 22.49, "普通体重");

        assertEquals("2026-08-01,170.0,65.0,22.49,普通体重", record.toCsvLine());
    }

    @Test
    void fromCsvLineParsesAllFields() {
        BmiRecord record = BmiRecord.fromCsvLine("2026-08-01,170.0,65.0,22.49,普通体重");

        assertEquals(LocalDate.of(2026, 8, 1), record.getDate());
        assertEquals(170.0, record.getHeightCm());
        assertEquals(65.0, record.getWeightKg());
        assertEquals(22.49, record.getBmi());
        assertEquals("普通体重", record.getCategory());
    }
}
