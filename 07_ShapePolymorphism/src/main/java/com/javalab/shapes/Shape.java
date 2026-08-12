package com.javalab.shapes;

/**
 * 図形の共通基底クラス。{@link Measurable} で定義された {@code area()}/{@code perimeter()} は
 * 図形ごとに計算方法が異なるため各サブクラスに実装を委ねるが、それらを組み合わせて説明文字列を
 * 組み立てる {@link #describe()} は図形の種類によらず共通のため、abstract classに実装をまとめている。
 */
public abstract class Shape implements Measurable {

    /**
     * @return 図形名(例: "円", "長方形", "三角形")
     */
    public abstract String getName();

    /**
     * 図形名・面積・周囲長をまとめた説明文字列を生成する。
     * @return 説明文字列(例: "長方形: 面積=20.00, 周囲=18.00")
     */
    public String describe() {
        return String.format("%s: 面積=%.2f, 周囲=%.2f", getName(), area(), perimeter());
    }
}
