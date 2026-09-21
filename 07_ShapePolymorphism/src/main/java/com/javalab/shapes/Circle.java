package com.javalab.shapes;

import java.util.Objects;

/**
 * 円。半径から面積・周囲長を計算する。
 */
public class Circle extends Shape {

    private final double radius;

    /**
     * @param radius 半径
     * @throws ShapeException radiusが負の場合
     */
    public Circle(double radius) {
        if (radius < 0) {
            throw new ShapeException("半径は0以上である必要があります: " + radius);
        }
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }

    @Override
    public String getName() {
        return "円";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Circle other)) {
            return false;
        }
        return Double.compare(radius, other.radius) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius);
    }

    @Override
    public String toString() {
        return "Circle{radius=" + radius + "}";
    }
}
