package com.javalab.designpatterns.factory;

/**
 * 円(Factoryパターンにおける具象製品の1つ)。
 * @param radius 半径
 */
public record Circle(double radius) implements Shape {

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
