package com.javalab.genericcollection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link GenericStack} のLIFO(後入れ先出し)動作と空スタックの異常系を検証するテスト。
 */
class GenericStackTest {

    @Test
    void pushAndPopReturnValuesInLifoOrder() {
        // A→B→Cの順にpushしたものを取り出すと、最後にpushしたCから逆順(C,B,A)で出てくる
        // ことを確認する(スタック=LIFOの本質的な検証)。
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
