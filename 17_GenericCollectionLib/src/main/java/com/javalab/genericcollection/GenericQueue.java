package com.javalab.genericcollection;

/**
 * 任意の型{@code T}を格納できる汎用キュー(FIFO)。
 * {@code java.util.Queue}等をラップせず、head/tailを管理する単方向連結リストで自前実装している。
 * @param <T> 格納する要素の型
 */
public class GenericQueue<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    private static class Node<T> {
        final T value;
        Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

    /**
     * 要素をキューの末尾に追加する。
     * @param value 追加する値
     */
    public void enqueue(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        size++;
    }

    /**
     * キューの先頭要素を取り出して削除する。
     * @return 取り出した値
     * @throws EmptyCollectionException キューが空の場合
     */
    public T dequeue() {
        if (head == null) {
            throw new EmptyCollectionException("キューが空です");
        }
        T value = head.value;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return value;
    }

    /**
     * キューの先頭要素を削除せずに参照する。
     * @return 先頭の値
     * @throws EmptyCollectionException キューが空の場合
     */
    public T peek() {
        if (head == null) {
            throw new EmptyCollectionException("キューが空です");
        }
        return head.value;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }
}
