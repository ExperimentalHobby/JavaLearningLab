package com.javalab.algorithmsdatastructures;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortAlgorithmsTest {

    @Test
    void selectionSort_sortsUnsortedListWithDuplicates() {
        List<Integer> input = List.of(5, 3, 8, 3, 1);

        List<Integer> sorted = SortAlgorithms.selectionSort(input);

        assertEquals(List.of(1, 3, 3, 5, 8), sorted);
    }

    @Test
    void selectionSort_emptyList_returnsEmptyList() {
        assertEquals(List.<Integer>of(), SortAlgorithms.selectionSort(List.<Integer>of()));
    }

    @Test
    void insertionSort_sortsUnsortedListWithDuplicates() {
        List<Integer> input = List.of(5, 3, 8, 3, 1);

        assertEquals(List.of(1, 3, 3, 5, 8), SortAlgorithms.insertionSort(input));
    }

    @Test
    void insertionSort_emptyList_returnsEmptyList() {
        assertEquals(List.<Integer>of(), SortAlgorithms.insertionSort(List.<Integer>of()));
    }

    @Test
    void quickSort_sortsUnsortedListWithDuplicates() {
        List<Integer> input = List.of(5, 3, 8, 3, 1);

        assertEquals(List.of(1, 3, 3, 5, 8), SortAlgorithms.quickSort(input));
    }

    @Test
    void quickSort_emptyList_returnsEmptyList() {
        assertEquals(List.<Integer>of(), SortAlgorithms.quickSort(List.<Integer>of()));
    }

    @Test
    void quickSort_singleElementList_returnsSameSingleElement() {
        assertEquals(List.of(42), SortAlgorithms.quickSort(List.of(42)));
    }

    @Test
    void mergeSort_sortsUnsortedListWithDuplicates() {
        List<Integer> input = List.of(5, 3, 8, 3, 1);

        assertEquals(List.of(1, 3, 3, 5, 8), SortAlgorithms.mergeSort(input));
    }

    @Test
    void mergeSort_emptyList_returnsEmptyList() {
        assertEquals(List.<Integer>of(), SortAlgorithms.mergeSort(List.<Integer>of()));
    }

    @Test
    void mergeSort_singleElementList_returnsSameSingleElement() {
        assertEquals(List.of(42), SortAlgorithms.mergeSort(List.of(42)));
    }

    @Test
    void selectionSortWithMetrics_returnsSortedListAndPositiveComparisons() {
        List<Integer> input = List.of(5, 3, 8, 3, 1);

        SortResult<Integer> result = SortAlgorithms.selectionSortWithMetrics(input);

        assertEquals(List.of(1, 3, 3, 5, 8), result.sorted());
        // selection sortは要素数nに対し常にn*(n-1)/2回比較する(nは5)。
        assertEquals(10, result.metrics().comparisons());
    }

    @Test
    void insertionSortWithMetrics_sortedInput_hasZeroMoves() {
        // 既にソート済みの入力ではシフト(move)が一切発生しないはず。
        List<Integer> input = List.of(1, 2, 3, 4, 5);

        SortResult<Integer> result = SortAlgorithms.insertionSortWithMetrics(input);

        assertEquals(List.of(1, 2, 3, 4, 5), result.sorted());
        assertEquals(0, result.metrics().moves());
    }

    @Test
    void quickSortWithMetrics_returnsSortedListAndPositiveComparisons() {
        List<Integer> input = List.of(5, 3, 8, 3, 1);

        SortResult<Integer> result = SortAlgorithms.quickSortWithMetrics(input);

        assertEquals(List.of(1, 3, 3, 5, 8), result.sorted());
        assertTrue(result.metrics().comparisons() > 0);
    }

    @Test
    void quickSort_sortedInput_doesNotStackOverflow() {
        // Lomuto分割で末尾要素を常にピボットにすると、ソート済み入力でO(n)の再帰深さになり
        // 大きい入力でStackOverflowErrorになっていた。ランダムピボットにより緩和されることを確認する。
        List<Integer> sortedInput = java.util.stream.IntStream.rangeClosed(1, 50_000).boxed().toList();

        List<Integer> result = SortAlgorithms.quickSort(sortedInput);

        assertEquals(sortedInput, result);
    }

    @Test
    void mergeSortWithMetrics_returnsSortedListAndPositiveMoves() {
        List<Integer> input = List.of(5, 3, 8, 3, 1);

        SortResult<Integer> result = SortAlgorithms.mergeSortWithMetrics(input);

        assertEquals(List.of(1, 3, 3, 5, 8), result.sorted());
        assertTrue(result.metrics().moves() > 0);
    }
}
