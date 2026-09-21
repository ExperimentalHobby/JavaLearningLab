package com.javalab.shapes;

import java.util.Objects;

/**
 * 長方形。幅・高さから面積・周囲長を計算する。
 */
public class Rectangle extends Shape {

    private final double width;
    private final double height;

    /**
     * @param width 幅
     * @param height 高さ
     * @throws ShapeException widthまたはheightが0以下の場合
     */
    public Rectangle(double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new ShapeException("幅・高さは0より大きい必要があります: " + width + ", " + height);
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2 * (width + height);
    }

    @Override
    public String getName() {
        return "長方形";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rectangle other)) {
            return false;
        }
        return Double.compare(width, other.width) == 0 && Double.compare(height, other.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return "Rectangle{width=" + width + ", height=" + height + "}";
    }
}
