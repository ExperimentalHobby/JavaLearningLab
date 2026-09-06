package com.javalab.algorithmsdatastructures;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinarySearchTreeTest {

    @Test
    void insertThenContains_insertedValue_returnsTrue() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(5);

        assertTrue(tree.contains(5));
    }

    @Test
    void contains_notInsertedValue_returnsFalse() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(5);

        assertFalse(tree.contains(999));
    }

    @Test
    void inOrderTraversal_returnsSortedList() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        List.of(5, 3, 8, 1, 4).forEach(tree::insert);

        assertEquals(List.of(1, 3, 4, 5, 8), tree.inOrderTraversal());
    }

    @Test
    void size_duplicateInsert_doesNotIncreaseSize() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(5);
        tree.insert(5);

        assertEquals(1, tree.size());
    }

    @Test
    void emptyTree_isEmptyTrueAndSizeZero() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
    }
}
