package com.javalab.genericcollection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link CollectionUtils#max(List)} の境界型パラメータ({@code T extends Comparable<T>})を
 * 使ったジェネリックメソッドを検証するテスト。Integer/Stringという異なる型に対して
 * 同じmax()メソッドが型安全に動作することを確認する。
 */
class CollectionUtilsTest {

    @Test
    void maxReturnsLargestIntegerInList() {
        int result = CollectionUtils.max(List.of(3, 1, 4, 1, 5, 9, 2, 6));

        assertEquals(9, result);
    }

    @Test
    void maxReturnsLargestStringInList() {
        // Stringの自然順序(辞書順)で最大の"cherry"が選ばれることを確認する。
        String result = CollectionUtils.max(List.of("banana", "apple", "cherry"));

        assertEquals("cherry", result);
    }

    @Test
    void maxThrowsIllegalArgumentExceptionForEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> CollectionUtils.max(List.<Integer>of()));
    }
}
