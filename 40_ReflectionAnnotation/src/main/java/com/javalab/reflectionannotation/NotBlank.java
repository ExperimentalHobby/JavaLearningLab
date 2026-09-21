package com.javalab.reflectionannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 付与した{@code String}フィールドがnull・空文字・空白のみでないことを要求する。 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlank {
    /** 違反メッセージ。{@code {field}}はフィールド名に置換される。 */
    String message() default "{field}は空白にできません";
}
