package com.javalab.genericcollection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenericStackTest {

    @Test
    void pushAndPopReturnValuesInLifoOrder() {
        GenericStack<String> stack = new GenericStack<>();

        stack.push("A");
        stack.push("B");
        stack.push("C");

        assertEquals("C", stack.pop());
        assertEquals("B", stack.pop());
        assertEquals("A", stack.pop());
    }

    @Test
    void popOnEmptyStackThrowsEmptyCollectionException() {
        GenericStack<String> stack = new GenericStack<>();

        assertThrows(EmptyCollectionException.class, stack::pop);
    }

    @Test
    void peekOnEmptyStackThrowsEmptyCollectionException() {
        GenericStack<String> stack = new GenericStack<>();

        assertThrows(EmptyCollectionException.class, stack::peek);
    }
}
