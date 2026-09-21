package com.javalab.bmi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    /**
     * 現在の記録一覧を返す。呼び出し側からの{@code add}/{@code remove}/{@code clear}を防ぐため、
     * 内部の{@link ArrayList}をそのまま返さず変更不可なコピーを返す。
     * @return 記録一覧(変更不可)
     */
    public List<BmiRecord> getRecords() {
        return List.copyOf(records);
    }

    /**
     * 平均BMIを返す。
     * @return 全記録のBMIの平均値
     * @throws IllegalStateException 記録が1件もない場合
     */
    public double averageBmi() {
        if (records.isEmpty()) {
            throw new IllegalStateException("履歴がありません");
        }
        return records.stream().mapToDouble(BmiRecord::getBmi).average().orElseThrow();
    }

    /**
     * BMIが最大の記録を返す。同値が複数ある場合は最初に追加された記録を返す。
     * @return BMI最大の記録。記録が1件もない場合は空。
     */
    public Optional<BmiRecord> maxRecord() {
        return records.stream().max(Comparator.comparingDouble(BmiRecord::getBmi));
    }

    /**
     * BMIが最小の記録を返す。同値が複数ある場合は最初に追加された記録を返す。
     * @return BMI最小の記録。記録が1件もない場合は空。
     */
    public Optional<BmiRecord> minRecord() {
        return records.stream().min(Comparator.comparingDouble(BmiRecord::getBmi));
    }

    /**
     * 指定期間(両端含む)の記録のみを絞り込んで返す。
     * @param from 期間の開始日
     * @param to 期間の終了日
     * @return 期間内の記録一覧(挿入順を維持)
     */
    public List<BmiRecord> getRecords(LocalDate from, LocalDate to) {
        return records.stream()
                .filter(record -> !record.getDate().isBefore(from) && !record.getDate().isAfter(to))
                .toList();
    }

    /**
     * 現在の履歴をCSVファイルに書き出す(1行目はヘッダ、以降1行1記録)。
     * @param file 書き込み先ファイル
     * @throws IOException 書き込みに失敗した場合
     */
    public void saveTo(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine(); // ヘッダ行を読み飛ばす
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(BmiRecord.fromCsvLine(line));
            }
        }
    }
}
