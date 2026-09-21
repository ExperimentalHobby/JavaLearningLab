package com.javalab.algorithmsdatastructures;

import java.util.List;

/**
 * ソート結果と実行統計をまとめたもの。
 * @param sorted ソート済みリスト
 * @param metrics 実行統計
 */
public record SortResult<T>(List<T> sorted, SortMetrics metrics) {
}
