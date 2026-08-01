package com.javalab.bmi;

import java.time.LocalDate;

/**
 * ある日時点のBMI測定記録(日付・身長・体重・BMI・判定区分)。
 */
public class BmiRecord {

    private final LocalDate date;
    private final double heightCm;
    private final double weightKg;
    private final double bmi;
    private final String category;

    public BmiRecord(LocalDate date, double heightCm, double weightKg, double bmi, String category) {
        this.date = date;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.bmi = bmi;
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public double getBmi() {
        return bmi;
    }

    public String getCategory() {
        return category;
    }

    /**
     * CSVの1行形式に変換する(例: {@code "2026-08-01,170.0,65.0,22.49,普通体重"})。
     * @return CSV形式の文字列
     */
    public String toCsvLine() {
        return date + "," + heightCm + "," + weightKg + "," + bmi + "," + category;
    }

    /**
     * {@link #toCsvLine()} で書き出した形式の1行からBmiRecordを復元する。
     * @param line CSVファイルから読み込んだ1行
     * @return 復元されたBmiRecord
     */
    public static BmiRecord fromCsvLine(String line) {
        String[] parts = line.split(",", 5);
        LocalDate parsedDate = LocalDate.parse(parts[0]);
        double parsedHeightCm = Double.parseDouble(parts[1]);
        double parsedWeightKg = Double.parseDouble(parts[2]);
        double parsedBmi = Double.parseDouble(parts[3]);
        String parsedCategory = parts[4];
        return new BmiRecord(parsedDate, parsedHeightCm, parsedWeightKg, parsedBmi, parsedCategory);
    }
}
