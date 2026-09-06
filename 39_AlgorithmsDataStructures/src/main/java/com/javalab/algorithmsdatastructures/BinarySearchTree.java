package com.javalab.algorithmsdatastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * 自前実装の二分探索木。
 * 削除({@code delete})は境界ケースが多く実装コストが高いため、今回のスコープ外とする。
 */
public class BinarySearchTree<T extends Comparable<T>> {

    private static final class Node<T> {
        private final T value;
        private Node<T> left;
        private Node<T> right;

        private Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root;
    private int size;

    /** 既に同じ値が存在する場合は何もしない(重複を許さない)。 */
    public void insert(T value) {
        root = insert(root, value);
    }

    private Node<T> insert(Node<T> node, T value) {
        if (node == null) {
            size++;
            return new Node<>(value);
        }
        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = insert(node.left, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, value);
        }
        return node;
    }

    public boolean contains(T value) {
        return contains(root, value);
    }

    private boolean contains(Node<T> node, T value) {
        if (node == null) {
            return false;
        }
        int cmp = value.compareTo(node.value);
        if (cmp == 0) {
            return true;
        }
        return cmp < 0 ? contains(node.left, value) : contains(node.right, value);
    }

    /** 通りがけ順(in-order)で辿ると、木の正しさを表す昇順のリストになる。 */
    public List<T> inOrderTraversal() {
        List<T> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(Node<T> node, List<T> result) {
        if (node == null) {
            return;
        }
        inOrder(node.left, result);
        result.add(node.value);
        inOrder(node.right, result);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
