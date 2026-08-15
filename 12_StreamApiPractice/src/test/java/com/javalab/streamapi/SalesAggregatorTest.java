package com.javalab.streamapi;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link SalesAggregator} の各種Stream API集計メソッドを検証するテスト。
 * 「果物」2件・「野菜」1件という共通のテストデータに対し、
 * 合計・カテゴリ別集計・絞り込み・平均・最大値・重複除去ソートをそれぞれ確認する。
 */
class SalesAggregatorTest {

    private final List<SalesRecord> records = List.of(
            new SalesRecord("りんご", "果物", new BigDecimal("100"), 3),
            new SalesRecord("バナナ", "果物", new BigDecimal("50"), 5),
            new SalesRecord("キャベツ", "野菜", new BigDecimal("200"), 1));

    @Test
    void totalSalesSumsAllAmounts() {
        // 100 + 50 + 200 = 350(map+reduceによる合算)。
        BigDecimal total = SalesAggregator.totalSales(records);

        assertEquals(0, new BigDecimal("350").compareTo(total));
    }

    @Test
    void totalQuantitySumsAllQuantities() {
        // 3 + 5 + 1 = 9(mapToInt+sumによる合算)。
        int total = SalesAggregator.totalQuantity(records);

        assertEquals(9, total);
    }

    @Test
    void totalByCategorySumsAmountsPerCategory() {
        // 果物(りんご100+バナナ50=150)と野菜(キャベツ200)がそれぞれ別集計になることを確認する
        // (Collectors.groupingBy + reducingの組み合わせ)。
        Map<String, BigDecimal> totals = SalesAggregator.totalByCategory(records);

        assertEquals(0, new BigDecimal("150").compareTo(totals.get("果物")));
        assertEquals(0, new BigDecimal("200").compareTo(totals.get("野菜")));
    }

    @Test
    void filterByCategoryReturnsOnlyMatchingRecords() {
        // 「果物」カテゴリの2件(りんご・バナナ)のみが元の順序を保ったまま抽出されることを確認する。
        List<SalesRecord> fruits = SalesAggregator.filterByCategory(records, "果物");

        assertEquals(2, fruits.size());
        assertEquals("りんご", fruits.get(0).product());
        assertEquals("バナナ", fruits.get(1).product());
    }

    @Test
    void filterAboveAmountReturnsOnlyRecordsAtOrAboveThreshold() {
        // 閾値100"以上"(>=)のため、ちょうど100のりんごも含まれることを確認する(境界値)。
        List<SalesRecord> highValue = SalesAggregator.filterAboveAmount(records, new BigDecimal("100"));

        assertEquals(2, highValue.size());
        assertEquals("りんご", highValue.get(0).product());
        assertEquals("キャベツ", highValue.get(1).product());
    }

    @Test
    void averageAmountCalculatesCorrectAverage() {
        double average = SalesAggregator.averageAmount(records);

        assertEquals(350.0 / 3, average, 0.001);
    }

    @Test
    void maxByAmountReturnsRecordWithHighestAmount() {
        // 3件中、金額200のキャベツが最大であることを確認する。
        Optional<SalesRecord> max = SalesAggregator.maxByAmount(records);

        assertEquals("キャベツ", max.orElseThrow().product());
    }

    @Test
    void productNamesSortedReturnsDistinctSortedNames() {
        // 元の3件に加え、既存の"りんご"と同名の商品(金額違い)をもう1件追加した4件に対して、
        // distinct()により重複が除去され、3種類の商品名がソート済みで返ることを確認する。
        List<SalesRecord> withDuplicate = List.of(
                records.get(0),
                records.get(1),
                records.get(2),
                new SalesRecord("りんご", "果物", new BigDecimal("120"), 2));

        List<String> names = SalesAggregator.productNamesSorted(withDuplicate);

        assertEquals(List.of("りんご", "キャベツ", "バナナ"), names);
    }
}
