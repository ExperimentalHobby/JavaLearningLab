package com.javalab.shapes;

/**
 * 面積・周囲長を計算できることを表す契約。図形以外の「測定可能な」概念にも適用できるよう、
 * {@link Shape}(抽象クラス)とは分離したinterfaceとして定義している。
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
