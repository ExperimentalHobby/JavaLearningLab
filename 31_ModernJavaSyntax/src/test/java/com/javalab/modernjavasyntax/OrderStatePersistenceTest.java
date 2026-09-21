package com.javalab.modernjavasyntax;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link OrderStatePersistence}のJSON保存・読込を検証するテスト。
 * sealed interfaceである{@link OrderState}の4種類の実装(record)を、
 * Jacksonのポリモーフィックシリアライズ({@code @JsonTypeInfo}/{@code @JsonSubTypes})で
 * 型情報を保ったまま往復できることを確認する。
 */
class OrderStatePersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    void saveThenLoad_restoresAllOrderStateTypes() throws IOException {
        Map<String, OrderState> orders = new LinkedHashMap<>();
        orders.put("ORD-001", new OrderState.Placed(LocalDate.of(2026, 1, 1)));
        orders.put("ORD-002", new OrderState.Shipped(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 3)));
        orders.put("ORD-003", new OrderState.Delivered(LocalDate.of(2026, 1, 1), "TRACK-001", LocalDate.of(2026, 1, 5)));
        orders.put("ORD-004", new OrderState.Cancelled(LocalDate.of(2026, 1, 1), "在庫切れ"));
        Path file = tempDir.resolve("orders.json");

        OrderStatePersistence.save(orders, file);
        Map<String, OrderState> loaded = OrderStatePersistence.load(file);

        assertEquals(orders, loaded);
    }

    @Test
    void load_missingFile_throwsIllegalArgumentException() {
        Path missing = tempDir.resolve("missing.json");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> OrderStatePersistence.load(missing));

        assertEquals("ファイルが見つかりません: " + missing, ex.getMessage());
    }
}
