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

        assertEquals("trackingNumber must not be blank", ex.getMessage());
    }

    @Test
    void cancelled_blankReason_throwsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new OrderState.Cancelled(LocalDate.of(2026, 1, 1), ""));

        assertEquals("reason must not be blank", ex.getMessage());
    }
}
