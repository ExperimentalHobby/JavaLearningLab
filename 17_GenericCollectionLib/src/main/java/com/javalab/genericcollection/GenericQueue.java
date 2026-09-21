package com.javalab.genericcollection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

/**
 * 任意の型{@code T}を格納できる汎用キュー(FIFO)。
 * {@code java.util.Queue}等をラップせず、head/tailを管理する単方向連結リストで自前実装している。
 * {@link Iterable}を実装しているため拡張forで先頭(次にdequeueされる要素)から順に走査できる。
 * @param <T> 格納する要素の型
 */
public class GenericQueue<T> implements Iterable<T> {

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

    /**
     * 先頭(次にdequeueされる要素)から順に走査するイテレータを返す。
     * @return イテレータ
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T value = current.value;
                current = current.next;
                return value;
            }
        };
    }

    /**
     * @return 先頭から順に要素を並べた文字列(例: {@code "[A, B]"})
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (T value : this) {
            joiner.add(String.valueOf(value));
        }
        return joiner.toString();
    }
}
