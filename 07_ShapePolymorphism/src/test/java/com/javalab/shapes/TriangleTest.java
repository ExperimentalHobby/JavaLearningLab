package com.javalab.shapes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Triangle} のヘロンの公式による面積計算、周囲長計算、
 * および三角不等式違反のバリデーションを検証するテスト。
 */
class TriangleTest {

    @Test
    void calculatesAreaUsingHeronsFormula() {
        // 3-4-5の直角三角形は面積が(3×4)/2=6になることが分かっているため、
        // ヘロンの公式の実装が正しいかを検算しやすい代表的な例として使っている。
        Triangle triangle = new Triangle(3, 4, 5);

        assertEquals(6, triangle.area(), 0.001);
    }

    @Test
    void calculatesPerimeter() {
        Triangle triangle = new Triangle(3, 4, 5);

        assertEquals(12, triangle.perimeter());
    }

    @Test
    void violatingTriangleInequalityThrowsShapeException() {
        // 1+1=2 は 10 より小さいため三角不等式を満たさず、三角形として成立しない。
        // コンストラクタでこの検証が行われ、ShapeExceptionがスローされることを確認する。
        assertThrows(ShapeException.class, () -> new Triangle(1, 1, 10));
    }

    @Test
    void equalsAndHashCodeAreBasedOnSideLengths() {
        Triangle triangle1 = new Triangle(3, 4, 5);
        Triangle triangle2 = new Triangle(3, 4, 5);
        Triangle differentTriangle = new Triangle(5, 5, 5);

        assertEquals(triangle1, triangle2);
        assertEquals(triangle1.hashCode(), triangle2.hashCode());
        org.junit.jupiter.api.Assertions.assertNotEquals(triangle1, differentTriangle);
    }

    @Test
    void toStringContainsSideLengths() {
        Triangle triangle = new Triangle(3, 4, 5);

        String text = triangle.toString();
        org.junit.jupiter.api.Assertions.assertTrue(text.contains("3"));
        org.junit.jupiter.api.Assertions.assertTrue(text.contains("4"));
        org.junit.jupiter.api.Assertions.assertTrue(text.contains("5"));
    }
}
