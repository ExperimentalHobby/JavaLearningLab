package com.javalab.rpn;

/**
 * 不正なトークン・オペランド不足・不正な式など、RPN電卓固有のエラーを表す例外。
 */
public class RpnCalculatorException extends RuntimeException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public RpnCalculatorException(String message) {
        super(message);
    }
}
