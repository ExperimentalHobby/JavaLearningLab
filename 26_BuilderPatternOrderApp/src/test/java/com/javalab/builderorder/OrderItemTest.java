package com.javalab.builderorder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link OrderItem#subtotal()} の小計計算を検証するテスト。
 */
class OrderItemTest {

    @Test
    void subtotalMultipliesQuantityByUnitPrice() {
        OrderItem item = new OrderItem("ノート", 3, 150);

        assertEquals(450, item.subtotal());
    }
}
