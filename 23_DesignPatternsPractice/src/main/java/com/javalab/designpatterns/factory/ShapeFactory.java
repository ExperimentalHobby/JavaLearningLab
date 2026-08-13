package com.javalab.designpatterns.factory;

/**
 * タイプ文字列から具象{@link Shape}を生成するFactory。
 */
public class ShapeFactory {

    /**
     * @param type "circle"(半径1つ)または"rectangle"(幅・高さの2つ)
     * @param params 図形ごとに必要なパラメータ
     * @return 生成された{@link Shape}
     * @throws IllegalArgumentException 未知のタイプを指定した場合
     */
    public Shape create(String type, double... params) {
        return switch (type) {
            case "circle" -> new Circle(params[0]);
            case "rectangle" -> new Rectangle(params[0], params[1]);
            default -> throw new IllegalArgumentException("未知の図形タイプです: " + type);
        };
    }
}
