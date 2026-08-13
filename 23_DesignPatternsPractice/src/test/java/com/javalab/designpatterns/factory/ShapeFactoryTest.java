package com.javalab.designpatterns.factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShapeFactoryTest {

    private final ShapeFactory factory = new ShapeFactory();

    @Test
    void createReturnsCircleWithCorrectArea() {
        Shape shape = factory.create("circle", 2.0);

        assertInstanceOf(Circle.class, shape);
        assertEquals(Math.PI * 4.0, shape.area(), 0.0001);
    }

    @Test
    void createReturnsRectangleWithCorrectArea() {
        Shape shape = factory.create("rectangle", 3.0, 4.0);

        assertInstanceOf(Rectangle.class, shape);
        assertEquals(12.0, shape.area(), 0.0001);
    }

    @Test
    void createThrowsIllegalArgumentExceptionForUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> factory.create("triangle", 1.0));
    }
}
