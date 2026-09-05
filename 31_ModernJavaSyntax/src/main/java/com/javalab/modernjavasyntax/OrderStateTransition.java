package com.javalab.modernjavasyntax;

import java.time.LocalDate;

/**
 * {@link OrderState}の状態遷移を行うユーティリティ。
 * switchのrecordパターンで現在の状態を分解しつつ、許可された遷移元かどうかを判定する。
 */
public final class OrderStateTransition {

    private OrderStateTransition() {
    }

    /** {@code Placed}からのみ出荷可能。それ以外の状態からは{@link IllegalStateException}。 */
    public static OrderState ship(OrderState state, String trackingNumber, LocalDate shippedDate) {
        return switch (state) {
            case OrderState.Placed(var orderedDate) -> new OrderState.Shipped(orderedDate, trackingNumber, shippedDate);
            case OrderState.Shipped s -> throw new IllegalStateException("cannot ship from Shipped");
            case OrderState.Delivered d -> throw new IllegalStateException("cannot ship from Delivered");
            case OrderState.Cancelled c -> throw new IllegalStateException("cannot ship from Cancelled");
        };
    }

    /** {@code Shipped}からのみ配達完了にできる。それ以外の状態からは{@link IllegalStateException}。 */
    public static OrderState deliver(OrderState state, LocalDate deliveredDate) {
        return switch (state) {
            case OrderState.Shipped(var orderedDate, var trackingNumber, var shippedDate) ->
                    new OrderState.Delivered(orderedDate, trackingNumber, deliveredDate);
            case OrderState.Placed p -> throw new IllegalStateException("cannot deliver from Placed");
            case OrderState.Delivered d -> throw new IllegalStateException("cannot deliver from Delivered");
            case OrderState.Cancelled c -> throw new IllegalStateException("cannot deliver from Cancelled");
        };
    }

    /** {@code Placed}/{@code Shipped}からのみキャンセル可能({@code Delivered}後のキャンセルは不可)。 */
    public static OrderState cancel(OrderState state, String reason) {
        return switch (state) {
            case OrderState.Placed(var orderedDate) -> new OrderState.Cancelled(orderedDate, reason);
            case OrderState.Shipped(var orderedDate, var trackingNumber, var shippedDate) ->
                    new OrderState.Cancelled(orderedDate, reason);
            case OrderState.Delivered d -> throw new IllegalStateException("cannot cancel from Delivered");
            case OrderState.Cancelled c -> throw new IllegalStateException("cannot cancel from Cancelled");
        };
    }
}
