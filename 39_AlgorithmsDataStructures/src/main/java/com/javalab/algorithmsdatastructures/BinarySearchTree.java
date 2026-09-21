package com.javalab.algorithmsdatastructures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 自前実装の二分探索木。
 * 削除({@code delete})は境界ケースが多く実装コストが高いため、今回のスコープ外とする。
 * {@code insert}/{@code contains}/{@code inOrderTraversal}は反復(iterative)実装にしている。
 * 昇順・降順の大量挿入で木が片側に伸びて退化すると、再帰実装では深さに比例してスタックを消費し
 * {@code StackOverflowError}になりうるため(木のバランシング自体は行わないため退化そのものは残るが、
 * スタックを消費しない分クラッシュは避けられる)。
 */
public class BinarySearchTree<T extends Comparable<? super T>> {

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
        if (root == null) {
            root = new Node<>(value);
            size++;
            return;
        }
        Node<T> current = root;
        while (true) {
            int cmp = value.compareTo(current.value);
            if (cmp == 0) {
                return;
            }
            if (cmp < 0) {
                if (current.left == null) {
                    current.left = new Node<>(value);
                    size++;
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node<>(value);
                    size++;
                    return;
                }
                current = current.right;
            }
        }
    }

    public boolean contains(T value) {
        Node<T> current = root;
        while (current != null) {
            int cmp = value.compareTo(current.value);
            if (cmp == 0) {
                return true;
            }
            current = cmp < 0 ? current.left : current.right;
        }
        return false;
    }

    /** 通りがけ順(in-order)で辿ると、木の正しさを表す昇順のリストになる。 */
    public List<T> inOrderTraversal() {
        List<T> result = new ArrayList<>(size);
        Deque<Node<T>> stack = new ArrayDeque<>();
        Node<T> current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(current.value);
            current = current.right;
        }
        return result;
    }

    /** 先行順(pre-order): 自分自身→左部分木→右部分木の順に辿る。 */
    public List<T> preOrderTraversal() {
        List<T> result = new ArrayList<>(size);
        if (root == null) {
            return result;
        }
        Deque<Node<T>> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node<T> node = stack.pop();
            result.add(node.value);
            // 右を先にpushすることで、popの順序が左→右になる。
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }
        return result;
    }

    /** 後行順(post-order): 左部分木→右部分木→自分自身の順に辿る。 */
    public List<T> postOrderTraversal() {
        List<T> result = new ArrayList<>(size);
        if (root == null) {
            return result;
        }
        // pre-order(自分→右→左)の順で積んでから反転すると、post-order(左→右→自分)になる。
        Deque<Node<T>> stack = new ArrayDeque<>();
        Deque<T> output = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node<T> node = stack.pop();
            output.push(node.value);
            if (node.left != null) {
                stack.push(node.left);
            }
            if (node.right != null) {
                stack.push(node.right);
            }
        }
        return new ArrayList<>(output);
    }

    /** 幅優先順(level-order): 根に近い階層から順に辿る。 */
    public List<T> levelOrderTraversal() {
        List<T> result = new ArrayList<>(size);
        if (root == null) {
            return result;
        }
        Deque<Node<T>> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node<T> node = queue.poll();
            result.add(node.value);
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        }
        return result;
    }

    /**
     * 木の高さ(根から最も遠い葉までの辺の数)を返す。空の木は-1、単一ノードの木は0とする。
     * 反復実装ではなく再帰実装だが、高さの計算自体は木の深さに比例するスタック消費であり、
     * 巨大な退化木では他のメソッドと同様のリスクがあることに注意(この規模の学習用実装では許容する)。
     */
    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
