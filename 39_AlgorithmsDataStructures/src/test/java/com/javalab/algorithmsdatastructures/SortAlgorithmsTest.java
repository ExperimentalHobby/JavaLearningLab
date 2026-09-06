package com.javalab.algorithmsdatastructures;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
