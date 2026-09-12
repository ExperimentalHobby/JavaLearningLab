package com.javalab.jmhbenchmark;

/**
 * 自前実装の単方向連結リストによるスタック。標準コレクション({@link java.util.ArrayDeque}等)との
 * 性能比較用に、あえて自前実装として用意している。
 */
public class SimpleLinkedStack<T> {

    private static final class Node<T> {
        private final T value;
        private final Node<T> next;

        private Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;

    public void push(T value) {
        top = new Node<>(value, top);
        size++;
    }

    public T pop() {
        if (top == null) {
            throw new IllegalStateException("stack is empty");
        }
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    public T peek() {
        if (top == null) {
            throw new IllegalStateException("stack is empty");
        }
        return top.value;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
}
