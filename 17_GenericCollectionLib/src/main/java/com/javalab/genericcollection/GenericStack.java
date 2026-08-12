package com.javalab.genericcollection;

/**
 * 任意の型{@code T}を格納できる汎用スタック(LIFO)。
 * {@code java.util.Stack}等をラップせず、単方向連結リストで自前実装している。
 * @param <T> 格納する要素の型
 */
public class GenericStack<T> {

    private Node<T> top;
    private int size;

    private static class Node<T> {
        final T value;
        final Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    /**
     * 要素をスタックの先頭に積む。
     * @param value 積む値
     */
    public void push(T value) {
        top = new Node<>(value, top);
        size++;
    }

    /**
     * スタックの先頭要素を取り出して削除する。
     * @return 取り出した値
     * @throws EmptyCollectionException スタックが空の場合
     */
    public T pop() {
        if (top == null) {
            throw new EmptyCollectionException("スタックが空です");
        }
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    /**
     * スタックの先頭要素を削除せずに参照する。
     * @return 先頭の値
     * @throws EmptyCollectionException スタックが空の場合
     */
    public T peek() {
        if (top == null) {
            throw new EmptyCollectionException("スタックが空です");
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
