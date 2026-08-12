package com.javalab.streamapi;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 売上レコードをStream APIで集計するユーティリティ。
 * すべて副作用のない静的メソッドとして実装し、map/filter/reduceなど中間・終端操作の
 * 組み合わせ方を示すことに主眼を置いている。
 */
public class SalesAggregator {

    /**
     * @param records 集計対象のレコード一覧
     * @return 金額合計(mapで金額を取り出し、reduceで合算)
     */
    public static BigDecimal totalSales(List<SalesRecord> records) {
        return records.stream()
                .map(SalesRecord::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @param records 集計対象のレコード一覧
     * @return 数量合計(mapToIntでプリミティブストリームに変換し、sumで合算)
     */
    public static int totalQuantity(List<SalesRecord> records) {
        return records.stream()
                .mapToInt(SalesRecord::quantity)
                .sum();
    }

    /**
     * カテゴリごとの金額合計を求める。
     * @param records 集計対象のレコード一覧
     * @return カテゴリ名をキー、金額合計を値とするMap
     */
    public static Map<String, BigDecimal> totalByCategory(List<SalesRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::category,
                        Collectors.reducing(BigDecimal.ZERO, SalesRecord::amount, BigDecimal::add)));
    }

    /**
     * @param records 絞り込み対象のレコード一覧
     * @param category 絞り込み対象のカテゴリ名
     * @return categoryに一致するレコードのみを含むリスト
     */
    public static List<SalesRecord> filterByCategory(List<SalesRecord> records, String category) {
        return records.stream()
                .filter(record -> record.category().equals(category))
                .toList();
    }

    /**
     * @param records 絞り込み対象のレコード一覧
     * @param threshold 閾値(この値以上の金額のレコードを抽出)
     * @return 金額がthreshold以上のレコードのみを含むリスト
     */
    public static List<SalesRecord> filterAboveAmount(List<SalesRecord> records, BigDecimal threshold) {
        return records.stream()
                .filter(record -> record.amount().compareTo(threshold) >= 0)
                .toList();
    }

    /**
     * @param records 集計対象のレコード一覧
     * @return 金額の平均値。recordsが空の場合は0.0
     */
    public static double averageAmount(List<SalesRecord> records) {
        return records.stream()
                .mapToDouble(record -> record.amount().doubleValue())
                .average()
                .orElse(0.0);
    }

    /**
     * @param records 集計対象のレコード一覧
     * @return 金額が最大のレコード。recordsが空の場合は空のOptional
     */
    public static Optional<SalesRecord> maxByAmount(List<SalesRecord> records) {
        return records.stream()
                .max(Comparator.comparing(SalesRecord::amount));
    }

    /**
     * @param records 集計対象のレコード一覧
     * @return 重複を除いた商品名を昇順に並べたリスト
     */
    public static List<String> productNamesSorted(List<SalesRecord> records) {
        return records.stream()
                .map(SalesRecord::product)
                .distinct()
                .sorted()
                .toList();
    }
}
