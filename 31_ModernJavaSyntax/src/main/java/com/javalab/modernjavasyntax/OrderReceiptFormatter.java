package com.javalab.modernjavasyntax;

/**
 * {@link OrderState}を人が読めるレシート文字列に整形する。
 * switchのrecordパターンで状態を分解し、text blockでレイアウトを組み立てる。
 */
public final class OrderReceiptFormatter {

    private OrderReceiptFormatter() {
    }

    public static String format(String orderId, OrderState state) {
        return switch (state) {
            case OrderState.Placed(var orderedDate) -> """
                    注文ID: %s
                    状態: 注文受付
                    注文日: %s
                    """.formatted(orderId, orderedDate);
            case OrderState.Shipped(var orderedDate, var trackingNumber, var shippedDate) -> """
                    注文ID: %s
                    状態: 発送済み
                    注文日: %s
                    発送日: %s
                    伝票番号: %s
                    """.formatted(orderId, orderedDate, shippedDate, trackingNumber);
            case OrderState.Delivered(var orderedDate, var trackingNumber, var deliveredDate) -> """
                    注文ID: %s
                    状態: 配達完了
                    注文日: %s
                    配達日: %s
                    伝票番号: %s
                    """.formatted(orderId, orderedDate, deliveredDate, trackingNumber);
            case OrderState.Cancelled(var orderedDate, var reason) -> """
                    注文ID: %s
                    状態: キャンセル
                    注文日: %s
                    理由: %s
                    """.formatted(orderId, orderedDate, reason);
        };
    }
}
