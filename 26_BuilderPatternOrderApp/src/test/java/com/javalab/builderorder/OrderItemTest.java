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

    @Test
    void subtotalDoesNotOverflowForLargeQuantityAndUnitPrice() {
        // int演算だとquantity * unitPriceがオーバーフローし、
        // 100000 * 100000 = 1410065408(本来10000000000)になってしまっていた問題への対応。
        OrderItem item = new OrderItem("高額商品", 100000, 100000);

        assertEquals(10_000_000_000L, item.subtotal());
    }
}
