package com.javalab.designpatterns.factory;

/**
 * タイプ文字列から具象{@link Shape}を生成するFactory。
 */
public class ShapeFactory {

    /**
     * @param type "circle"(半径1つ)または"rectangle"(幅・高さの2つ)
     * @param params 図形ごとに必要なパラメータ
     * @return 生成された{@link Shape}
     * @throws IllegalArgumentException 未知のタイプを指定した場合、またはパラメータの個数が不足している場合
     */
    public Shape create(String type, double... params) {
        return switch (type) {
            case "circle" -> new Circle(requireParams(type, params, 1, "半径")[0]);
            case "rectangle" -> {
                double[] p = requireParams(type, params, 2, "幅・高さ");
                yield new Rectangle(p[0], p[1]);
            }
            default -> throw new IllegalArgumentException("未知の図形タイプです: " + type);
        };
    }

    private double[] requireParams(String type, double[] params, int required, String paramDescription) {
        if (params.length != required) {
            throw new IllegalArgumentException(
                    type + "には" + paramDescription + "の" + required + "個のパラメータが必要です(指定された個数: "
                            + params.length + ")");
        }
        return params;
    }
}
