package com.javalab.jmhbenchmark;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleLinkedStackTest {

    @Test
    void pushThenPop_returnsInLifoOrder() {
        SimpleLinkedStack<Integer> stack = new SimpleLinkedStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void newStack_isEmptyWithSizeZero() {
        SimpleLinkedStack<Integer> stack = new SimpleLinkedStack<>();

        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }
}
