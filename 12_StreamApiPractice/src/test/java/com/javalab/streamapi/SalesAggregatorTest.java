package com.javalab.streamapi;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SalesAggregatorTest {

    private final List<SalesRecord> records = List.of(
            new SalesRecord("りんご", "果物", new BigDecimal("100"), 3),
            new SalesRecord("バナナ", "果物", new BigDecimal("50"), 5),
            new SalesRecord("キャベツ", "野菜", new BigDecimal("200"), 1));

    @Test
    void totalSalesSumsAllAmounts() {
        BigDecimal total = SalesAggregator.totalSales(records);

        assertEquals(0, new BigDecimal("350").compareTo(total));
    }

    @Test
    void totalQuantitySumsAllQuantities() {
        int total = SalesAggregator.totalQuantity(records);

        assertEquals(9, total);
    }

    @Test
    void totalByCategorySumsAmountsPerCategory() {
        Map<String, BigDecimal> totals = SalesAggregator.totalByCategory(records);

        assertEquals(0, new BigDecimal("150").compareTo(totals.get("果物")));
        assertEquals(0, new BigDecimal("200").compareTo(totals.get("野菜")));
    }

    @Test
    void filterByCategoryReturnsOnlyMatchingRecords() {
        List<SalesRecord> fruits = SalesAggregator.filterByCategory(records, "果物");

        assertEquals(2, fruits.size());
        assertEquals("りんご", fruits.get(0).product());
        assertEquals("バナナ", fruits.get(1).product());
    }

    @Test
    void filterAboveAmountReturnsOnlyRecordsAtOrAboveThreshold() {
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
        Optional<SalesRecord> max = SalesAggregator.maxByAmount(records);

        assertEquals("キャベツ", max.orElseThrow().product());
    }

    @Test
    void productNamesSortedReturnsDistinctSortedNames() {
        List<SalesRecord> withDuplicate = List.of(
                records.get(0),
                records.get(1),
                records.get(2),
                new SalesRecord("りんご", "果物", new BigDecimal("120"), 2));

        List<String> names = SalesAggregator.productNamesSorted(withDuplicate);

        assertEquals(List.of("りんご", "キャベツ", "バナナ"), names);
    }
}
