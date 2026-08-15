package com.javalab.bmi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link BmiHistory} の記録追加・CSVファイル保存/読込を検証するテスト。
 * ファイルI/Oは{@code @TempDir}が提供する実際の一時ディレクトリを使い、
 * モックではなく本物のファイルシステムに対して読み書きすることで結合的に確認している。
 */
class BmiHistoryTest {

    private final BmiHistory history = new BmiHistory();

    @TempDir
    Path tempDir;

    @Test
    void addComputesBmiAndCategoryThenAppendsRecord() {
        // add()は身長・体重からBMIと判定区分を自前で計算して記録するため、
        // 呼び出し側で計算済みの値を渡さなくても正しい結果が保存されることを確認する。
        history.add(LocalDate.of(2026, 8, 1), 170, 65);

        assertEquals(1, history.getRecords().size());
        BmiRecord record = history.getRecords().get(0);
        assertEquals(22.49, record.getBmi(), 0.001);
        assertEquals("普通体重", record.getCategory());
    }

    @Test
    void saveToAndLoadFromRoundTripsRecords() throws IOException {
        // 区分が異なる2件(普通体重・肥満(1度))を保存し、別のBmiHistoryインスタンスへ
        // 読み込んだ結果が元の内容・順序と完全に一致することを確認する「ラウンドトリップテスト」。
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
