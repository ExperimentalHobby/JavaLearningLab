package com.javalab.genericcollection;

import java.util.List;

/**
 * ジェネリクスを使った汎用コレクション操作を提供するユーティリティクラス。
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * リスト中の最大値を返す。{@code T}に{@code Comparable<T>}の境界型パラメータを課すことで、
     * 比較可能な任意の型に対して型安全に動作する。
     * @param list 対象リスト
     * @param <T> 比較可能な要素の型
     * @return 最大値
     * @throws IllegalArgumentException リストが空の場合
     */
    public static <T extends Comparable<T>> T max(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("空のリストの最大値は取得できません");
        }
        T max = list.get(0);
        for (T item : list) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }
}
