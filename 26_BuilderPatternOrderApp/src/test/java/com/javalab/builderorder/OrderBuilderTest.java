package com.javalab.builderorder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Order.Builder} によるBuilderパターンの実装(必須項目の検証・オプション項目の既定値・
 * フルーエントAPI)を検証するテスト。
 */
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

    @Test
    void addItemThrowsIllegalArgumentExceptionForNonPositiveQuantity() {
        // quantity -3で小計が-300円になってしまっていた問題への対応。
        Order.Builder builder = new Order.Builder("山田太郎", "東京都渋谷区1-1-1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.addItem("ノート", -3, 100));
        assertEquals("数量は1以上である必要があります: -3", exception.getMessage());
    }

    @Test
    void addItemThrowsIllegalArgumentExceptionForNegativeUnitPrice() {
        Order.Builder builder = new Order.Builder("山田太郎", "東京都渋谷区1-1-1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.addItem("ノート", 3, -100));
        assertEquals("単価は0以上である必要があります: -100", exception.getMessage());
    }

    @Test
    void noteThrowsIllegalArgumentExceptionForNull() {
        // note(null)した注文をtoSummary()するとnote.isEmpty()でNPEになっていた問題への対応。
        Order.Builder builder = new Order.Builder("山田太郎", "東京都渋谷区1-1-1");

        assertThrows(IllegalArgumentException.class, () -> builder.note(null));
    }

    @Test
    void paymentMethodThrowsIllegalArgumentExceptionForNull() {
        Order.Builder builder = new Order.Builder("山田太郎", "東京都渋谷区1-1-1");

        assertThrows(IllegalArgumentException.class, () -> builder.paymentMethod(null));
    }

    @Test
    void equalsReturnsTrueForOrdersWithSameFieldValues() {
        Order first = new Order.Builder("山田太郎", "東京都渋谷区1-1-1").addItem("ノート", 3, 150).build();
        Order second = new Order.Builder("山田太郎", "東京都渋谷区1-1-1").addItem("ノート", 3, 150).build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsReturnsFalseForOrdersWithDifferentFieldValues() {
        Order first = new Order.Builder("山田太郎", "東京都渋谷区1-1-1").addItem("ノート", 3, 150).build();
        Order second = new Order.Builder("鈴木花子", "東京都渋谷区1-1-1").addItem("ノート", 3, 150).build();

        assertNotEquals(first, second);
    }

    @Test
    void toStringContainsKeyFieldValues() {
        Order order = new Order.Builder("山田太郎", "東京都渋谷区1-1-1").addItem("ノート", 3, 150).build();

        String text = order.toString();

        assertTrue(text.contains("山田太郎"));
        assertTrue(text.contains("東京都渋谷区1-1-1"));
    }

    @Test
    void buildThrowsIllegalStateExceptionWhenCalledTwice() {
        // build()を呼んだ後も同じBuilderを使い回せてしまっていた問題への対応。
        Order.Builder builder = new Order.Builder("山田太郎", "東京都渋谷区1-1-1").addItem("ノート", 3, 150);
        builder.build();

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void addItemThrowsIllegalStateExceptionAfterBuild() {
        Order.Builder builder = new Order.Builder("山田太郎", "東京都渋谷区1-1-1").addItem("ノート", 3, 150);
        builder.build();

        assertThrows(IllegalStateException.class, () -> builder.addItem("ペン", 1, 100));
    }
}
