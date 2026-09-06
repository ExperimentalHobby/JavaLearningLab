package com.javalab.modernjavasyntax;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderReceiptFormatterTest {

    @Test
    void format_placed_showsOrderedDateOnly() {
        OrderState placed = new OrderState.Placed(LocalDate.of(2026, 1, 1));

        String receipt = OrderReceiptFormatter.format("ORD-001", placed);

        assertEquals("""
                注文ID: ORD-001
                状態: 注文受付
                注文日: 2026-01-01
                """, receipt);
    }

    @Test
    void format_shipped_showsTrackingNumberAndShippedDate() {
        OrderState shipped = new OrderState.Shipped(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 3));

        String receipt = OrderReceiptFormatter.format("ORD-001", shipped);

        assertEquals("""
                注文ID: ORD-001
                状態: 発送済み
                注文日: 2026-01-01
                発送日: 2026-01-03
                伝票番号: TRACK-001
                """, receipt);
    }

    @Test
    void format_delivered_showsDeliveredDate() {
        OrderState delivered = new OrderState.Delivered(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 5));

        String receipt = OrderReceiptFormatter.format("ORD-001", delivered);

        assertEquals("""
                注文ID: ORD-001
                状態: 配達完了
                注文日: 2026-01-01
                配達日: 2026-01-05
                伝票番号: TRACK-001
                """, receipt);
    }

    @Test
    void format_cancelled_showsReason() {
        OrderState cancelled = new OrderState.Cancelled(LocalDate.of(2026, 1, 1), "在庫切れ");

        String receipt = OrderReceiptFormatter.format("ORD-001", cancelled);

        assertEquals("""
                注文ID: ORD-001
                状態: キャンセル
                注文日: 2026-01-01
                理由: 在庫切れ
                """, receipt);
    }
}
