package com.javalab.jpmsmodule.impl;

import com.javalab.jpmsmodule.api.Greeter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JapaneseGreeterTest {

    @Test
    void greet_returnsJapaneseGreeting() {
        Greeter greeter = new JapaneseGreeter();

        assertEquals("こんにちは、太郎さん!", greeter.greet("太郎"));
    }
}
