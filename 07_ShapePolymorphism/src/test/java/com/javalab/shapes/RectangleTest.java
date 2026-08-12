package com.javalab.shapes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RectangleTest {

    @Test
    void calculatesArea() {
        Rectangle rectangle = new Rectangle(4, 5);

        assertEquals(20, rectangle.area());
    }

    @Test
    void calculatesPerimeter() {
        Rectangle rectangle = new Rectangle(4, 5);

        assertEquals(18, rectangle.perimeter());
    }
}
