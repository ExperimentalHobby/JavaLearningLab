package com.javalab.bmi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * BMI測定履歴をArrayListで管理し、java.ioでCSVファイルへの保存・読込を行う。
 */
public class BmiHistory {

    private static final String CSV_HEADER = "date,height_cm,weight_kg,bmi,category";

    private final List<BmiRecord> records = new ArrayList<>();

    /**
     * 身長・体重からBMI・判定区分を計算し、新しい記録として追加する。
     * @param date 測定日
     * @param heightCm 身長(cm)
     * @param weightKg 体重(kg)
     */
    public void add(LocalDate date, double heightCm, double weightKg) {
        double bmi = BmiCalculator.calculate(heightCm, weightKg);
        String category = BmiCalculator.classify(bmi);
        records.add(new BmiRecord(date, heightCm, weightKg, bmi, category));
    }

    public List<BmiRecord> getRecords() {
        return records;
    }

    /**
     * 現在の履歴をCSVファイルに書き出す(1行目はヘッダ、以降1行1記録)。
     * @param file 書き込み先ファイル
     * @throws IOException 書き込みに失敗した場合
     */
    public void saveTo(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSV_HEADER);
            writer.newLine();
            for (BmiRecord record : records) {
                writer.write(record.toCsvLine());
                writer.newLine();
            }
        }
    }

    /**
     * CSVファイルから履歴を読み込み、現在の記録を置き換える。
     * @param file 読み込み元ファイル
     * @throws IOException 読み込みに失敗した場合
     */
    public void loadFrom(File file) throws IOException {
        // 読込前にクリアすることで、load後の状態がファイル内容と完全に一致するようにする。
        records.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // ヘッダ行を読み飛ばす
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(BmiRecord.fromCsvLine(line));
            }
        }
    }
}
