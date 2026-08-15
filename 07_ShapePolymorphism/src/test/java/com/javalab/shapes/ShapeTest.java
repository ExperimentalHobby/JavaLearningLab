package com.javalab.shapes;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link Shape}によるポリモーフィズム(多態性)そのものを検証するテスト。
 * 個々の図形クラス(Circle/Rectangle/Triangle)の計算式は各テストクラスで検証済みのため、
 * ここでは「呼び出し側が図形の具体的な型を意識せずShape型として一律に扱えること」を確認する。
 */
class ShapeTest {

    @Test
    void sumsAreaOfDifferentShapesPolymorphically() {
        // List<Shape>にCircle/Rectangle/Triangleという異なるクラスのインスタンスを混在させても、
        // shape.area()の呼び出しはコンパイル時の型(Shape)ではなく実行時の実際の型に応じて
        // 正しいarea()実装(オーバーライドされたメソッド)にディスパッチされることを確認する。
        List<Shape> shapes = List.of(new Circle(5), new Rectangle(4, 5), new Triangle(3, 4, 5));

        double totalArea = 0;
        for (Shape shape : shapes) {
            totalArea += shape.area();
        }

        assertEquals(78.54 + 20 + 6, totalArea, 0.01);
    }

    @Test
    void describeCombinesNameAreaAndPerimeter() {
        // describe()はShape(抽象クラス)側に実装があり、getName()/area()/perimeter()という
        // サブクラスごとに異なる値を組み合わせて共通フォーマットの文字列を組み立てることを確認する。
        Rectangle rectangle = new Rectangle(4, 5);

        assertEquals("長方形: 面積=20.00, 周囲=18.00", rectangle.describe());
    }
}
