package com.javalab.algorithmsdatastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 代表的なソートアルゴリズムの実装比較用ユーティリティ。
 * いずれも入力の{@link List}は変更せず、ソート済みの新しいリストを返す。
 */
public final class SortAlgorithms {

    private SortAlgorithms() {
    }

    /** 計算量O(n^2)。未整列部分から最小値を探して先頭に確定させていく。 */
    public static <T extends Comparable<T>> List<T> selectionSort(List<T> input) {
        List<T> result = new ArrayList<>(input);
        int n = result.size();
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (result.get(j).compareTo(result.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }
            Collections.swap(result, i, minIndex);
        }
        return result;
    }

    /** 計算量O(n^2)。整列済み部分に新しい要素を適切な位置へ挿入していく。 */
    public static <T extends Comparable<T>> List<T> insertionSort(List<T> input) {
        List<T> result = new ArrayList<>(input);
        for (int i = 1; i < result.size(); i++) {
            T key = result.get(i);
            int j = i - 1;
            while (j >= 0 && result.get(j).compareTo(key) > 0) {
                result.set(j + 1, result.get(j));
                j--;
            }
            result.set(j + 1, key);
        }
        return result;
    }

    /** 平均計算量O(n log n)。末尾要素をピボットに分割統治する(Lomuto分割)。 */
    public static <T extends Comparable<T>> List<T> quickSort(List<T> input) {
        List<T> result = new ArrayList<>(input);
        quickSort(result, 0, result.size() - 1);
        return result;
    }

    private static <T extends Comparable<T>> void quickSort(List<T> list, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(list, low, high);
            quickSort(list, low, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, high);
        }
    }

    private static <T extends Comparable<T>> int partition(List<T> list, int low, int high) {
        T pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    /** 計算量O(n log n)。半分に分割して再帰的にソートし、整列済みの2つを併合する。 */
    public static <T extends Comparable<T>> List<T> mergeSort(List<T> input) {
        if (input.size() <= 1) {
            return new ArrayList<>(input);
        }
        int mid = input.size() / 2;
        List<T> left = mergeSort(input.subList(0, mid));
        List<T> right = mergeSort(input.subList(mid, input.size()));
        return merge(left, right);
    }

    private static <T extends Comparable<T>> List<T> merge(List<T> left, List<T> right) {
        List<T> result = new ArrayList<>(left.size() + right.size());
        int i = 0;
        int j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).compareTo(right.get(j)) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        result.addAll(left.subList(i, left.size()));
        result.addAll(right.subList(j, right.size()));
        return result;
    }
}
