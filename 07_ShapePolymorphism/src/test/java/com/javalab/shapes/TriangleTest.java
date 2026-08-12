package com.javalab.shapes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TriangleTest {

    @Test
    void calculatesAreaUsingHeronsFormula() {
        Triangle triangle = new Triangle(3, 4, 5);

        assertEquals(6, triangle.area(), 0.001);
    }

    @Test
    void calculatesPerimeter() {
        Triangle triangle = new Triangle(3, 4, 5);

        assertEquals(12, triangle.perimeter());
    }

    @Test
    void violatingTriangleInequalityThrowsShapeException() {
        assertThrows(ShapeException.class, () -> new Triangle(1, 1, 10));
    }
}
