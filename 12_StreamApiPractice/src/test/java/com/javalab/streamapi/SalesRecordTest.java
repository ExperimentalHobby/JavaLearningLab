package com.javalab.streamapi;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link SalesRecord} のコンストラクタ検証(金額・数量の負値チェック)を検証するテスト。
 */
class SalesRecordTest {

    @Test
    void throwsExceptionForNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> new SalesRecord("りんご", "果物", new BigDecimal("-100"), 3));
    }

    @Test
    void throwsExceptionForNegativeQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> new SalesRecord("りんご", "果物", new BigDecimal("100"), -1));
    }
}
