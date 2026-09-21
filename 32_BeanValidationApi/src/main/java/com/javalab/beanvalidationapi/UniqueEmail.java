package com.javalab.beanvalidationapi;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * メールアドレスが未登録であることを検証するカスタム制約アノテーション。
 * 標準の{@code @Email}が「形式」しか検証できないのに対し、これは既存データ({@link UserService})を
 * 参照する必要がある「業務検証」の例になる(標準アノテーションだけでは書けない検証の実装例)。
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {

    String message() default "{user.email.unique}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
