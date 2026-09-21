package com.javalab.genericcollection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * ジェネリクスを使った汎用コレクション操作を提供するユーティリティクラス。
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * リスト中の最大値を返す。境界型パラメータには{@code T extends Comparable<T>}ではなく
     * PECS(Producer Extends, Consumer Super)に沿った{@code T extends Comparable<? super T>}を
     * 使うことで、{@code T}自身ではなく{@code T}の親クラスが{@code Comparable}を実装している型
     * (継承階層の途中でComparableを実装するケース)でも呼び出せるようにしている。
     * @param list 対象リスト
     * @param <T> 比較可能な要素の型
     * @return 最大値
     * @throws EmptyCollectionException リストが空の場合
     */
    public static <T extends Comparable<? super T>> T max(List<T> list) {
        if (list.isEmpty()) {
            throw new EmptyCollectionException("空のリストの最大値は取得できません");
        }
        T max = list.get(0);
        for (T item : list) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }

    /**
     * リスト中の最小値を返す。
     * @param list 対象リスト
     * @param <T> 比較可能な要素の型
     * @return 最小値
     * @throws EmptyCollectionException リストが空の場合
     */
    public static <T extends Comparable<? super T>> T min(List<T> list) {
        if (list.isEmpty()) {
            throw new EmptyCollectionException("空のリストの最小値は取得できません");
        }
        T min = list.get(0);
        for (T item : list) {
            if (item.compareTo(min) < 0) {
                min = item;
            }
        }
        return min;
    }

    /**
     * 条件に一致する要素だけを抽出した新しいリストを返す。
     * @param list 対象リスト
     * @param predicate 抽出条件
     * @param <T> 要素の型
     * @return predicateがtrueを返す要素のみを元の順序で含むリスト
     */
    public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 各要素を変換した新しいリストを返す。
     * @param list 対象リスト
     * @param mapper 変換関数
     * @param <T> 変換前の要素の型
     * @param <R> 変換後の要素の型
     * @return 変換後の要素を元の順序で含むリスト
     */
    public static <T, R> List<R> map(List<T> list, Function<? super T, ? extends R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(mapper.apply(item));
        }
        return result;
    }
}
