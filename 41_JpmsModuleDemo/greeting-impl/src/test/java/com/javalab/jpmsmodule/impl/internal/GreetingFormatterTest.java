package com.javalab.jpmsmodule.impl.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreetingFormatterTest {

    @Test
    void format_returnsJapaneseGreeting() {
        String result = GreetingFormatter.format("太郎");

        assertEquals("こんにちは、太郎さん!", result);
    }
}
