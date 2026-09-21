package com.javalab.shapes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Circle} の面積・周囲長計算、および不正な半径のバリデーションを検証するテスト。
 */
class CircleTest {

    @Test
    void calculatesArea() {
        // 半径5の円の面積 = π×5^2 ≒ 78.54。
        Circle circle = new Circle(5);

        assertEquals(78.54, circle.area(), 0.01);
    }

    @Test
    void calculatesPerimeter() {
        // 半径5の円周 = 2×π×5 ≒ 31.42。
        Circle circle = new Circle(5);

        assertEquals(31.42, circle.perimeter(), 0.01);
    }

    @Test
    void negativeRadiusThrowsShapeException() {
        // 負の半径は物理的に成立しないため、コンストラクタでShapeExceptionをスローする。
        assertThrows(ShapeException.class, () -> new Circle(-1));
    }

    @Test
    void equalsAndHashCodeAreBasedOnRadius() {
        // 同じ半径のCircleは等価とみなせることを確認する(equals/hashCodeが未定義だった問題への対応)。
        Circle circle1 = new Circle(5);
        Circle circle2 = new Circle(5);
        Circle differentCircle = new Circle(3);

        assertEquals(circle1, circle2);
        assertEquals(circle1.hashCode(), circle2.hashCode());
        org.junit.jupiter.api.Assertions.assertNotEquals(circle1, differentCircle);
    }

    @Test
    void toStringContainsRadius() {
        Circle circle = new Circle(5);

        org.junit.jupiter.api.Assertions.assertTrue(circle.toString().contains("5"));
    }
}
