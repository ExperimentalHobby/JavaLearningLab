package com.javalab.modernjavasyntax;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderStateTest {

    @Test
    void shipped_blankTrackingNumber_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new OrderState.Shipped(LocalDate.of(2026, 1, 1), " ", LocalDate.of(2026, 1, 2)));

        assertEquals("伝票番号は空にできません", ex.getMessage());
    }

    @Test
    void cancelled_blankReason_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new OrderState.Cancelled(LocalDate.of(2026, 1, 1), ""));

        assertEquals("理由は空にできません", ex.getMessage());
    }

    @Test
    void placed_nullOrderedDate_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new OrderState.Placed(null));

        assertEquals("注文日は必須です", ex.getMessage());
    }

    @Test
    void delivered_nullOrderedDate_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new OrderState.Delivered(null, "TRACK-001", LocalDate.of(2026, 1, 5)));

        assertEquals("注文日は必須です", ex.getMessage());
    }

    @Test
    void delivered_blankTrackingNumber_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new OrderState.Delivered(LocalDate.of(2026, 1, 1), " ", LocalDate.of(2026, 1, 5)));

        assertEquals("伝票番号は空にできません", ex.getMessage());
    }

    @Test
    void delivered_nullDeliveredDate_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new OrderState.Delivered(LocalDate.of(2026, 1, 1), "TRACK-001", null));

        assertEquals("配達日は必須です", ex.getMessage());
    }
}
