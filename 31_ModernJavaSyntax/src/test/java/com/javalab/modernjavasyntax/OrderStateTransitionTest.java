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
    void ship_fromShipped_throwsIllegalStateExceptionWithJapaneseMessage() {
        OrderState shipped = new OrderState.Shipped(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 3));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> OrderStateTransition.ship(shipped, "TRACK-002", LocalDate.of(2026, 1, 4)));

        assertEquals("発送済みの注文は発送できません", ex.getMessage());
    }

    @Test
    void deliver_fromShipped_transitionsToDelivered() {
        OrderState shipped = new OrderState.Shipped(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 3));

        OrderState result = OrderStateTransition.deliver(shipped, LocalDate.of(2026, 1, 5));

        assertEquals(new OrderState.Delivered(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 5)), result);
    }

    @Test
    void deliver_fromPlaced_throwsIllegalStateExceptionWithJapaneseMessage() {
        OrderState placed = new OrderState.Placed(LocalDate.of(2026, 1, 1));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> OrderStateTransition.deliver(placed, LocalDate.of(2026, 1, 5)));

        assertEquals("注文受付の注文は配達完了にできません", ex.getMessage());
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
    void cancel_fromDelivered_throwsIllegalStateExceptionWithJapaneseMessage() {
        OrderState delivered = new OrderState.Delivered(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 5));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> OrderStateTransition.cancel(delivered, "返品希望"));

        assertEquals("配達完了の注文はキャンセルできません", ex.getMessage());
    }
}
