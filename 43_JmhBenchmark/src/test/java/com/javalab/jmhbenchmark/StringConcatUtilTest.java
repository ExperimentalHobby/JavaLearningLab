package com.javalab.jmhbenchmark;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringConcatUtilTest {

    @Test
    void concatWithPlusOperator_joinsAllPartsInOrder() {
        String result = StringConcatUtil.concatWithPlusOperator(List.of("a", "b", "c"));

        assertEquals("abc", result);
    }

    @Test
    void concatWithStringBuilder_joinsAllPartsInOrder() {
        String result = StringConcatUtil.concatWithStringBuilder(List.of("a", "b", "c"));

        assertEquals("abc", result);
    }
}
