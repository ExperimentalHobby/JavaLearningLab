package com.javalab.builderorder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderBuilderTest {

    @Test
    void buildCreatesOrderWithRequiredFieldsAndOneItem() {
        Order order = new Order.Builder("山田太郎", "東京都渋谷区1-1-1")
                .addItem("ノート", 3, 150)
                .build();

        assertEquals("山田太郎", order.customerName());
        assertEquals("東京都渋谷区1-1-1", order.shippingAddress());
        assertEquals(1, order.items().size());
    }

    @Test
    void totalAmountSumsSubtotalsOfAllItems() {
        Order order = new Order.Builder("山田太郎", "東京都渋谷区1-1-1")
                .addItem("ノート", 3, 150)
                .addItem("ペン", 2, 100)
                .build();

        assertEquals(650, order.totalAmount());
    }

    @Test
    void buildUsesDefaultValuesWhenOptionalFieldsAreNotSpecified() {
        Order order = new Order.Builder("山田太郎", "東京都渋谷区1-1-1")
                .addItem("ノート", 3, 150)
                .build();

        assertEquals("代金引換", order.paymentMethod());
        assertEquals(false, order.giftWrapping());
        assertEquals("", order.note());
    }

    @Test
    void buildReflectsSpecifiedOptionalFields() {
        Order order = new Order.Builder("山田太郎", "東京都渋谷区1-1-1")
                .addItem("ノート", 3, 150)
                .paymentMethod("クレジットカード")
                .giftWrap(true)
                .note("割れ物注意")
                .build();

        assertEquals("クレジットカード", order.paymentMethod());
        assertEquals(true, order.giftWrapping());
        assertEquals("割れ物注意", order.note());
    }

    @Test
    void buildThrowsIllegalStateExceptionWhenNoItemsAdded() {
        Order.Builder builder = new Order.Builder("山田太郎", "東京都渋谷区1-1-1");

        IllegalStateException exception = assertThrows(IllegalStateException.class, builder::build);
        assertEquals("商品が1件も追加されていません", exception.getMessage());
    }

    @Test
    void constructorThrowsIllegalArgumentExceptionWhenCustomerNameIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Order.Builder(" ", "東京都渋谷区1-1-1"));
        assertEquals("顧客名は必須です", exception.getMessage());
    }

    @Test
    void constructorThrowsIllegalArgumentExceptionWhenShippingAddressIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Order.Builder("山田太郎", ""));
        assertEquals("配送先住所は必須です", exception.getMessage());
    }
}
