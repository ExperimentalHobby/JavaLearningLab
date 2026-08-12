package com.javalab.shapes;

/**
 * 三角形。3辺の長さから面積(ヘロンの公式)・周囲長を計算する。
 */
public class Triangle extends Shape {

    private final double sideA;
    private final double sideB;
    private final double sideC;

    /**
     * @param sideA 辺の長さ
     * @param sideB 辺の長さ
     * @param sideC 辺の長さ
     * @throws ShapeException 3辺が三角不等式(2辺の和が残り1辺より大きい)を満たさない場合
     */
    public Triangle(double sideA, double sideB, double sideC) {
        if (sideA + sideB <= sideC || sideB + sideC <= sideA || sideC + sideA <= sideB) {
            throw new ShapeException(
                    "三角不等式を満たしません: " + sideA + ", " + sideB + ", " + sideC);
        }
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
    }

    @Override
    public double area() {
        // ヘロンの公式: s = 周囲長の半分、面積 = √(s(s-a)(s-b)(s-c))
        double s = perimeter() / 2;
        return Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC));
    }

    @Override
    public double perimeter() {
        return sideA + sideB + sideC;
    }

    @Override
    public String getName() {
        return "三角形";
    }
}
