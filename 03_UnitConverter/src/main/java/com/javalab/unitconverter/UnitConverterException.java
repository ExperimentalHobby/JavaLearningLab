package com.javalab.unitconverter;

/**
 * 不明な単位や異なるカテゴリ間の変換など、単位変換ツール固有のエラーを表す例外。
 */
public class UnitConverterException extends RuntimeException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public UnitConverterException(String message) {
        super(message);
    }
}
