package com.javalab.shapes;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShapeTest {

    @Test
    void sumsAreaOfDifferentShapesPolymorphically() {
        List<Shape> shapes = List.of(new Circle(5), new Rectangle(4, 5), new Triangle(3, 4, 5));

        double totalArea = 0;
        for (Shape shape : shapes) {
            totalArea += shape.area();
        }

        assertEquals(78.54 + 20 + 6, totalArea, 0.01);
    }

    @Test
    void describeCombinesNameAreaAndPerimeter() {
        Rectangle rectangle = new Rectangle(4, 5);

        assertEquals("長方形: 面積=20.00, 周囲=18.00", rectangle.describe());
    }
}
