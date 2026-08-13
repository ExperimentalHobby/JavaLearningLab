package com.javalab.designpatterns.factory;

public record Circle(double radius) implements Shape {

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}
