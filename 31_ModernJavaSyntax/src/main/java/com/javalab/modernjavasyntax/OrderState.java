package com.javalab.modernjavasyntax;

import java.time.LocalDate;

/**
 * 注文の配送状態を表すsealed interface。
 * 取りうる状態を{@link Placed}/{@link Shipped}/{@link Delivered}/{@link Cancelled}の4種類に限定することで、
 * switch式での分岐漏れをコンパイル時に検出できるようにする。
 */
public sealed interface OrderState permits OrderState.Placed, OrderState.Shipped, OrderState.Delivered, OrderState.Cancelled {

    record Placed(LocalDate orderedDate) implements OrderState {
    }

    record Shipped(LocalDate orderedDate, String trackingNumber, LocalDate shippedDate) implements OrderState {
        public Shipped {
            if (trackingNumber == null || trackingNumber.isBlank()) {
                throw new IllegalArgumentException("trackingNumber must not be blank");
            }
        }
    }

    record Delivered(LocalDate orderedDate, String trackingNumber, LocalDate deliveredDate) implements OrderState {
    }

    record Cancelled(LocalDate orderedDate, String reason) implements OrderState {
        public Cancelled {
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("reason must not be blank");
            }
        }
    }
}
