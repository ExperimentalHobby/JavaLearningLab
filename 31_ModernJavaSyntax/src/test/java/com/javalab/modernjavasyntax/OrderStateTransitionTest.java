package com.javalab.modernjavasyntax;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderStateTransitionTest {

    @Test
    void ship_fromPlaced_transitionsToShipped() {
        OrderState placed = new OrderState.Placed(LocalDate.of(2026, 1, 1));

        OrderState result = OrderStateTransition.ship(placed, "TRACK-001", LocalDate.of(2026, 1, 3));

        assertEquals(new OrderState.Shipped(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 3)), result);
    }

    @Test
    void ship_fromShipped_throwsIllegalStateException() {
        OrderState shipped = new OrderState.Shipped(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 3));

        assertThrows(IllegalStateException.class,
                () -> OrderStateTransition.ship(shipped, "TRACK-002", LocalDate.of(2026, 1, 4)));
    }

    @Test
    void deliver_fromShipped_transitionsToDelivered() {
        OrderState shipped = new OrderState.Shipped(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 3));

        OrderState result = OrderStateTransition.deliver(shipped, LocalDate.of(2026, 1, 5));

        assertEquals(new OrderState.Delivered(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 5)), result);
    }

    @Test
    void deliver_fromPlaced_throwsIllegalStateException() {
        OrderState placed = new OrderState.Placed(LocalDate.of(2026, 1, 1));

        assertThrows(IllegalStateException.class, () -> OrderStateTransition.deliver(placed, LocalDate.of(2026, 1, 5)));
    }

    @Test
    void cancel_fromPlaced_transitionsToCancelled() {
        OrderState placed = new OrderState.Placed(LocalDate.of(2026, 1, 1));

        OrderState result = OrderStateTransition.cancel(placed, "在庫切れ");

        assertEquals(new OrderState.Cancelled(LocalDate.of(2026, 1, 1), "在庫切れ"), result);
    }

    @Test
    void cancel_fromShipped_transitionsToCancelled() {
        OrderState shipped = new OrderState.Shipped(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 3));

        OrderState result = OrderStateTransition.cancel(shipped, "顧客都合");

        assertEquals(new OrderState.Cancelled(LocalDate.of(2026, 1, 1), "顧客都合"), result);
    }

    @Test
    void cancel_fromDelivered_throwsIllegalStateException() {
        OrderState delivered = new OrderState.Delivered(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 5));

        assertThrows(IllegalStateException.class, () -> OrderStateTransition.cancel(delivered, "返品希望"));
    }
}
