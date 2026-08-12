package com.javalab.shapes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CircleTest {

    @Test
    void calculatesArea() {
        Circle circle = new Circle(5);

        assertEquals(78.54, circle.area(), 0.01);
    }

    @Test
    void calculatesPerimeter() {
        Circle circle = new Circle(5);

        assertEquals(31.42, circle.perimeter(), 0.01);
    }

    @Test
    void negativeRadiusThrowsShapeException() {
        assertThrows(ShapeException.class, () -> new Circle(-1));
    }
}
