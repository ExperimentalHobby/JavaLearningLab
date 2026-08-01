package com.javalab.bmi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BmiHistoryTest {

    private final BmiHistory history = new BmiHistory();

    @TempDir
    Path tempDir;

    @Test
    void addComputesBmiAndCategoryThenAppendsRecord() {
        history.add(LocalDate.of(2026, 8, 1), 170, 65);

        assertEquals(1, history.getRecords().size());
        BmiRecord record = history.getRecords().get(0);
        assertEquals(22.49, record.getBmi(), 0.001);
        assertEquals("普通体重", record.getCategory());
    }

    @Test
    void saveToAndLoadFromRoundTripsRecords() throws IOException {
        history.add(LocalDate.of(2026, 8, 1), 170, 65);
        history.add(LocalDate.of(2026, 8, 2), 170, 80);
        File file = tempDir.resolve("bmi.csv").toFile();

        history.saveTo(file);
        BmiHistory loaded = new BmiHistory();
        loaded.loadFrom(file);

        assertEquals(2, loaded.getRecords().size());
        assertEquals(LocalDate.of(2026, 8, 1), loaded.getRecords().get(0).getDate());
        assertEquals("普通体重", loaded.getRecords().get(0).getCategory());
        assertEquals(LocalDate.of(2026, 8, 2), loaded.getRecords().get(1).getDate());
        assertEquals("肥満(1度)", loaded.getRecords().get(1).getCategory());
    }
}
