package com.javalab.builderorder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemTest {

    @Test
    void subtotalMultipliesQuantityByUnitPrice() {
        OrderItem item = new OrderItem("ノート", 3, 150);

        assertEquals(450, item.subtotal());
    }
}
