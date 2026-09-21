package com.javalab.genericcollection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link GenericQueue} のFIFO(先入れ先出し)動作と空キューの異常系を検証するテスト。
 */
class GenericQueueTest {

    @Test
    void enqueueAndDequeueReturnValuesInFifoOrder() {
        // A→B→Cの順にenqueueしたものを取り出すと、最初にenqueueしたAから順(A,B,C)に
        // 出てくることを確認する(キュー=FIFOの本質的な検証。スタックのLIFOと対比できる)。
        GenericQueue<String> queue = new GenericQueue<>();

        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertEquals("A", queue.dequeue());
        assertEquals("B", queue.dequeue());
        assertEquals("C", queue.dequeue());
    }

    @Test
    void dequeueOnEmptyQueueThrowsEmptyCollectionException() {
        GenericQueue<String> queue = new GenericQueue<>();

        assertThrows(EmptyCollectionException.class, queue::dequeue);
    }

    @Test
    void peekOnEmptyQueueThrowsEmptyCollectionException() {
        GenericQueue<String> queue = new GenericQueue<>();

        assertThrows(EmptyCollectionException.class, queue::peek);
    }

    @Test
    void toStringShowsElementsFromHeadToTail() {
        GenericQueue<String> queue = new GenericQueue<>();
        queue.enqueue("A");
        queue.enqueue("B");

        assertEquals("[A, B]", queue.toString());
    }

    @Test
    void iteratesElementsFromHeadToTail() {
        GenericQueue<String> queue = new GenericQueue<>();
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        List<String> collected = new ArrayList<>();
        for (String value : queue) {
            collected.add(value);
        }

        assertEquals(List.of("A", "B", "C"), collected);
    }
}
