package com.javalab.algorithmsdatastructures;

/**
 * ソートアルゴリズムの実行統計。
 * @param comparisons 比較回数({@code compareTo}呼び出し回数)
 * @param moves 要素の移動回数。「交換(swap)」という用語はselection/quickソートにしか正確に当てはまらず、
 *              insertionソートは「シフト」、mergeソートは「書き込み」であるため、共通の用語として使う
 * @param elapsedNanos 所要時間(ナノ秒)
 */
public record SortMetrics(long comparisons, long moves, long elapsedNanos) {
}
