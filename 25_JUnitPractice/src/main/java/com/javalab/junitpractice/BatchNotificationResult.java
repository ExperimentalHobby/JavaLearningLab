package com.javalab.junitpractice;

import java.util.List;

/**
 * {@link OrderNotificationService#notifyOrders(List)} の結果。
 * 修正前は1件の送信失敗で例外が伝播し残りの注文へ送信されなかったため、
 * 部分失敗を集約して返す設計にした(一括送信の結果を呼び出し側が把握できるようにする)。
 * @param sentCount 送信できた件数
 * @param skippedCount スキップした件数(合計金額0円以下・メールアドレス不正)
 * @param failures 送信に失敗した注文ごとの例外一覧
 */
public record BatchNotificationResult(int sentCount, int skippedCount, List<OrderNotificationException> failures) {

    public BatchNotificationResult {
        failures = List.copyOf(failures);
    }
}
