package com.javalab.designpatterns.factory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link ShapeFactory#create(String, double...)} のFactoryパターン実装を検証するテスト。
 * タイプ文字列に応じて正しい具象クラス({@link Circle}/{@link Rectangle})が
 * 生成されることと、未知のタイプでは例外になることを確認する。
 */
class ShapeFactoryTest {

    private final ShapeFactory factory = new ShapeFactory();

    @Test
    void createReturnsCircleWithCorrectArea() {
        Shape shape = factory.create("circle", 2.0);

        assertInstanceOf(Circle.class, shape);
        assertEquals(Math.PI * 4.0, shape.area(), 0.0001);
    }

    @Test
    void createReturnsRectangleWithCorrectArea() {
        Shape shape = factory.create("rectangle", 3.0, 4.0);

        assertInstanceOf(Rectangle.class, shape);
        assertEquals(12.0, shape.area(), 0.0001);
    }

    @Test
    void createThrowsIllegalArgumentExceptionForUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> factory.create("triangle", 1.0));
    }

    @Test
    void createThrowsIllegalArgumentExceptionWithClearMessageWhenCircleParamsAreMissing() {
        // 半径を省略した場合、修正前はShapeFactory内部のparams[0]でArrayIndexOutOfBoundsExceptionになり、
        // 「エラー: Index 0 out of bounds for length 0」という英語の内部例外メッセージが
        // 利用者に表示されてしまっていた。分かりやすいIllegalArgumentExceptionにする。
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> factory.create("circle"));

        assertEquals("circleには半径の1個のパラメータが必要です(指定された個数: 0)", exception.getMessage());
    }

    @Test
    void createThrowsIllegalArgumentExceptionWithClearMessageWhenRectangleParamsAreMissing() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> factory.create("rectangle", 3.0));

        assertEquals("rectangleには幅・高さの2個のパラメータが必要です(指定された個数: 1)", exception.getMessage());
    }
}
