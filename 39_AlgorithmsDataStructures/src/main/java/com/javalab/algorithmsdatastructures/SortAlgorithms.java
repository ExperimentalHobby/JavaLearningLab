package com.javalab.algorithmsdatastructures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 代表的なソートアルゴリズムの実装比較用ユーティリティ。
 * いずれも入力の{@link List}は変更せず、ソート済みの新しいリストを返す。
 * 比較回数・移動回数・所要時間を計測したい場合は{@code xxxWithMetrics}系メソッドを使う。
 */
public final class SortAlgorithms {

    private SortAlgorithms() {
    }

    /** 計算量O(n^2)。未整列部分から最小値を探して先頭に確定させていく。 */
    public static <T extends Comparable<? super T>> List<T> selectionSort(List<T> input) {
        return selectionSortWithMetrics(input).sorted();
    }

    /** {@link #selectionSort}に加え、比較回数・交換回数・所要時間を計測する。 */
    public static <T extends Comparable<? super T>> SortResult<T> selectionSortWithMetrics(List<T> input) {
        long start = System.nanoTime();
        List<T> result = new ArrayList<>(input);
        long comparisons = 0;
        long moves = 0;
        int n = result.size();
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                comparisons++;
                if (result.get(j).compareTo(result.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                Collections.swap(result, i, minIndex);
                moves++;
            }
        }
        return new SortResult<>(result, new SortMetrics(comparisons, moves, System.nanoTime() - start));
    }

    /** 計算量O(n^2)。整列済み部分に新しい要素を適切な位置へ挿入していく。 */
    public static <T extends Comparable<? super T>> List<T> insertionSort(List<T> input) {
        return insertionSortWithMetrics(input).sorted();
    }

    /** {@link #insertionSort}に加え、比較回数・シフト回数・所要時間を計測する。 */
    public static <T extends Comparable<? super T>> SortResult<T> insertionSortWithMetrics(List<T> input) {
        long start = System.nanoTime();
        List<T> result = new ArrayList<>(input);
        long comparisons = 0;
        long moves = 0;
        for (int i = 1; i < result.size(); i++) {
            T key = result.get(i);
            int j = i - 1;
            while (j >= 0) {
                comparisons++;
                if (result.get(j).compareTo(key) <= 0) {
                    break;
                }
                result.set(j + 1, result.get(j));
                moves++;
                j--;
            }
            result.set(j + 1, key);
        }
        return new SortResult<>(result, new SortMetrics(comparisons, moves, System.nanoTime() - start));
    }

    /**
     * 平均計算量O(n log n)。ランダムに選んだ要素をピボットにして分割統治する(Lomuto分割)。
     * 末尾要素を常にピボットにすると、ソート済み・逆順の入力で毎回最も偏った分割になり、
     * 比較回数がO(n^2)・再帰の深さもO(n)になってStackOverflowErrorの原因になる。
     * ランダムピボットにより、入力の並びに依存する最悪ケースをほぼ回避できる。
     */
    public static <T extends Comparable<? super T>> List<T> quickSort(List<T> input) {
        return quickSortWithMetrics(input).sorted();
    }

    /** {@link #quickSort}に加え、比較回数・交換回数・所要時間を計測する。 */
    public static <T extends Comparable<? super T>> SortResult<T> quickSortWithMetrics(List<T> input) {
        long start = System.nanoTime();
        List<T> result = new ArrayList<>(input);
        Counters counters = new Counters();
        quickSort(result, 0, result.size() - 1, counters);
        return new SortResult<>(result, new SortMetrics(counters.comparisons, counters.moves, System.nanoTime() - start));
    }

    private static <T extends Comparable<? super T>> void quickSort(List<T> list, int low, int high, Counters counters) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, counters);
            quickSort(list, low, pivotIndex - 1, counters);
            quickSort(list, pivotIndex + 1, high, counters);
        }
    }

    private static <T extends Comparable<? super T>> int partition(List<T> list, int low, int high, Counters counters) {
        int randomIndex = ThreadLocalRandom.current().nextInt(low, high + 1);
        Collections.swap(list, randomIndex, high);
        counters.moves++;

        T pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            counters.comparisons++;
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;
                Collections.swap(list, i, j);
                counters.moves++;
            }
        }
        Collections.swap(list, i + 1, high);
        counters.moves++;
        return i + 1;
    }

    /** 計算量O(n log n)。半分に分割して再帰的にソートし、整列済みの2つを併合する。 */
    public static <T extends Comparable<? super T>> List<T> mergeSort(List<T> input) {
        return mergeSortWithMetrics(input).sorted();
    }

    /** {@link #mergeSort}に加え、比較回数・書き込み回数・所要時間を計測する。 */
    public static <T extends Comparable<? super T>> SortResult<T> mergeSortWithMetrics(List<T> input) {
        long start = System.nanoTime();
        Counters counters = new Counters();
        List<T> sorted = mergeSort(input, counters);
        return new SortResult<>(sorted, new SortMetrics(counters.comparisons, counters.moves, System.nanoTime() - start));
    }

    private static <T extends Comparable<? super T>> List<T> mergeSort(List<T> input, Counters counters) {
        if (input.size() <= 1) {
            return new ArrayList<>(input);
        }
        int mid = input.size() / 2;
        List<T> left = mergeSort(input.subList(0, mid), counters);
        List<T> right = mergeSort(input.subList(mid, input.size()), counters);
        return merge(left, right, counters);
    }

    private static <T extends Comparable<? super T>> List<T> merge(List<T> left, List<T> right, Counters counters) {
        List<T> result = new ArrayList<>(left.size() + right.size());
        int i = 0;
        int j = 0;
        while (i < left.size() && j < right.size()) {
            counters.comparisons++;
            if (left.get(i).compareTo(right.get(j)) <= 0) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
            counters.moves++;
        }
        while (i < left.size()) {
            result.add(left.get(i++));
            counters.moves++;
        }
        while (j < right.size()) {
            result.add(right.get(j++));
            counters.moves++;
        }
        return result;
    }

    /** 比較回数・移動回数を再帰呼び出しをまたいで積算するための可変カウンタ。 */
    private static final class Counters {
        long comparisons;
        long moves;
    }
}
