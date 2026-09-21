package com.javalab.shapes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Rectangle} の面積・周囲長計算を検証するテスト。
 */
class RectangleTest {

    @Test
    void calculatesArea() {
        Rectangle rectangle = new Rectangle(4, 5);

        assertEquals(20, rectangle.area());
    }

    @Test
    void calculatesPerimeter() {
        Rectangle rectangle = new Rectangle(4, 5);

        assertEquals(18, rectangle.perimeter());
    }

    @Test
    void throwsExceptionForNegativeWidth() {
        // CircleやTriangleはコンストラクタで不正な寸法を検証しているのに、Rectangleだけ
        // 検証がなく"rectangle -4 5"のような負の寸法がそのまま通り、面積-20.0の図形が登録できていた。
        assertThrows(ShapeException.class, () -> new Rectangle(-4, 5));
    }

    @Test
    void throwsExceptionForZeroHeight() {
        assertThrows(ShapeException.class, () -> new Rectangle(4, 0));
    }

    @Test
    void equalsAndHashCodeAreBasedOnWidthAndHeight() {
        Rectangle rectangle1 = new Rectangle(4, 5);
        Rectangle rectangle2 = new Rectangle(4, 5);
        Rectangle differentRectangle = new Rectangle(5, 4);

        assertEquals(rectangle1, rectangle2);
        assertEquals(rectangle1.hashCode(), rectangle2.hashCode());
        org.junit.jupiter.api.Assertions.assertNotEquals(rectangle1, differentRectangle);
    }

    @Test
    void toStringContainsWidthAndHeight() {
        Rectangle rectangle = new Rectangle(4, 5);

        String text = rectangle.toString();
        org.junit.jupiter.api.Assertions.assertTrue(text.contains("4"));
        org.junit.jupiter.api.Assertions.assertTrue(text.contains("5"));
    }
}
