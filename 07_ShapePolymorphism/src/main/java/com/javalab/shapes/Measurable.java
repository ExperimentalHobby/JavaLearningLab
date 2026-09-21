package com.javalab.shapes;

/**
 * 面積・周囲長を計算できることを表す契約。将来、図形以外の「測定可能な」概念にも
 * 適用できるようinterfaceとして分離しているが、現時点の実装は{@link Shape}のみ。
 */
public interface Measurable {

    /**
     * @return 面積
     */
    double area();

    /**
     * @return 周囲長
     */
    double perimeter();
}
