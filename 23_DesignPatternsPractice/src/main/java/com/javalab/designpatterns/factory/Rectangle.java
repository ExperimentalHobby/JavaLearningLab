package com.javalab.designpatterns.factory;

/**
 * 長方形(Factoryパターンにおける具象製品の1つ)。
 * @param width 幅
 * @param height 高さ
 */
public record Rectangle(double width, double height) implements Shape {

    @Override
    public double area() {
        return width * height;
    }
}
