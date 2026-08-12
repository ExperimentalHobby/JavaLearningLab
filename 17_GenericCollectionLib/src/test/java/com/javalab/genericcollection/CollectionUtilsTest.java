package com.javalab.genericcollection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CollectionUtilsTest {

    @Test
    void maxReturnsLargestIntegerInList() {
        int result = CollectionUtils.max(List.of(3, 1, 4, 1, 5, 9, 2, 6));

        assertEquals(9, result);
    }

    @Test
    void maxReturnsLargestStringInList() {
        String result = CollectionUtils.max(List.of("banana", "apple", "cherry"));

        assertEquals("cherry", result);
    }

    @Test
    void maxThrowsIllegalArgumentExceptionForEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> CollectionUtils.max(List.<Integer>of()));
    }
}
