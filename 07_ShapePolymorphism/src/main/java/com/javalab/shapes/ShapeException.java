package com.javalab.shapes;

/**
 * 負の寸法・三角不等式違反など、図形として成立しないパラメータを表す例外。
 */
public class ShapeException extends RuntimeException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public ShapeException(String message) {
        super(message);
    }
}
