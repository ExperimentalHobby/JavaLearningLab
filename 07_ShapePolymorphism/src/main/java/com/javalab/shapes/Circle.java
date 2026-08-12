package com.javalab.shapes;

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
}
