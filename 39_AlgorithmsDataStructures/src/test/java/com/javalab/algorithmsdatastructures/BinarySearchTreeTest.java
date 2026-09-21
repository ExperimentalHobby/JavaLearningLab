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

    @Test
    void insertContainsInOrder_manyAscendingValues_doesNotStackOverflow() {
        // 昇順に大量挿入すると木が片側に伸びて退化し、再帰実装ではStackOverflowErrorになっていた。
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        int count = 100_000;
        for (int i = 0; i < count; i++) {
            tree.insert(i);
        }

        assertEquals(count, tree.size());
        assertTrue(tree.contains(count - 1));
        assertEquals(count, tree.inOrderTraversal().size());
    }

    @Test
    void preOrderTraversal_returnsRootFirst() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        List.of(5, 3, 8, 1, 4).forEach(tree::insert);

        assertEquals(List.of(5, 3, 1, 4, 8), tree.preOrderTraversal());
    }

    @Test
    void postOrderTraversal_returnsRootLast() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        List.of(5, 3, 8, 1, 4).forEach(tree::insert);

        assertEquals(List.of(1, 4, 3, 8, 5), tree.postOrderTraversal());
    }

    @Test
    void levelOrderTraversal_returnsBreadthFirstOrder() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        List.of(5, 3, 8, 1, 4).forEach(tree::insert);

        assertEquals(List.of(5, 3, 8, 1, 4), tree.levelOrderTraversal());
    }

    @Test
    void height_emptyTree_returnsMinusOne() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();

        assertEquals(-1, tree.height());
    }

    @Test
    void height_singleNode_returnsZero() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(5);

        assertEquals(0, tree.height());
    }

    @Test
    void height_balancedThreeNodeTree_returnsOne() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        List.of(5, 3, 8).forEach(tree::insert);

        assertEquals(1, tree.height());
    }

    @Test
    void height_degenerateChain_returnsChainLength() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        List.of(1, 2, 3, 4).forEach(tree::insert);

        assertEquals(3, tree.height());
    }
}
