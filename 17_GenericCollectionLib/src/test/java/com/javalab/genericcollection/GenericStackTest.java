package com.javalab.genericcollection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void toStringShowsElementsFromTopToBottom() {
        // デバッグ時に中身を確認しづらかった問題への対応。
        GenericStack<String> stack = new GenericStack<>();
        stack.push("A");
        stack.push("B");

        assertEquals("[B, A]", stack.toString());
    }

    @Test
    void iteratesElementsFromTopToBottom() {
        // Iterable<T>を実装していないため拡張forが使えなかった問題への対応。
        GenericStack<String> stack = new GenericStack<>();
        stack.push("A");
        stack.push("B");
        stack.push("C");

        List<String> collected = new ArrayList<>();
        for (String value : stack) {
            collected.add(value);
        }

        assertEquals(List.of("C", "B", "A"), collected);
    }
}
