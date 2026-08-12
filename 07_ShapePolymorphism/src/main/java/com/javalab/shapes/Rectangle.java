package com.javalab.shapes;

/**
 * 長方形。幅・高さから面積・周囲長を計算する。
 */
public class Rectangle extends Shape {

    private final double width;
    private final double height;

    public Rectangle(double width, double height) {
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
}
