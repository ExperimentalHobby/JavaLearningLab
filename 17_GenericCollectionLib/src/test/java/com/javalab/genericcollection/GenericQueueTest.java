package com.javalab.genericcollection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenericQueueTest {

    @Test
    void enqueueAndDequeueReturnValuesInFifoOrder() {
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
}
