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
            case OrderState.Shipped s -> throw cannotTransition(s, "発送できません");
            case OrderState.Delivered d -> throw cannotTransition(d, "発送できません");
            case OrderState.Cancelled c -> throw cannotTransition(c, "発送できません");
        };
    }

    /** {@code Shipped}からのみ配達完了にできる。それ以外の状態からは{@link IllegalStateException}。 */
    public static OrderState deliver(OrderState state, LocalDate deliveredDate) {
        return switch (state) {
            case OrderState.Shipped(var orderedDate, var trackingNumber, var _) ->
                    new OrderState.Delivered(orderedDate, trackingNumber, deliveredDate);
            case OrderState.Placed p -> throw cannotTransition(p, "配達完了にできません");
            case OrderState.Delivered d -> throw cannotTransition(d, "配達完了にできません");
            case OrderState.Cancelled c -> throw cannotTransition(c, "配達完了にできません");
        };
    }

    /** {@code Placed}/{@code Shipped}からのみキャンセル可能({@code Delivered}後のキャンセルは不可)。 */
    public static OrderState cancel(OrderState state, String reason) {
        return switch (state) {
            case OrderState.Placed(var orderedDate) -> new OrderState.Cancelled(orderedDate, reason);
            case OrderState.Shipped(var orderedDate, var _, var _) ->
                    new OrderState.Cancelled(orderedDate, reason);
            case OrderState.Delivered d -> throw cannotTransition(d, "キャンセルできません");
            case OrderState.Cancelled c -> throw cannotTransition(c, "キャンセルできません");
        };
    }

    private static IllegalStateException cannotTransition(OrderState state, String actionMessage) {
        return new IllegalStateException(state.label() + "の注文は" + actionMessage);
    }
}
