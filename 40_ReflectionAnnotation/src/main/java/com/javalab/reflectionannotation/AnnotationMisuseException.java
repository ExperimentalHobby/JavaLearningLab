package com.javalab.reflectionannotation;

/**
 * 検証アノテーションが対応していない型のフィールドに付与されていた場合に送出する非チェック例外。
 * 利用者の入力値の問題({@link ValidationViolation})とは区別し、
 * プログラマがアノテーションを誤って使ったという「設定エラー」として扱う。
 */
public class AnnotationMisuseException extends RuntimeException {

    public AnnotationMisuseException(String message) {
        super(message);
    }
}
