package com.javalab.beanvalidationapi;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ユーザー登録リクエストのDTO。Bean Validationアノテーションで入力値を検証する。
 * @param name 氏名。空文字不可
 * @param email メールアドレス。空文字不可・メール形式であること
 * @param age 年齢。0〜150の範囲であること
 */
public record UserRegistrationRequest(
        @NotBlank(message = "{user.name.notBlank}") String name,
        @NotBlank(message = "{user.email.notBlank}") @Email(message = "{user.email.invalid}") @UniqueEmail String email,
        @NotNull(message = "{user.age.notNull}") @Min(value = 0, message = "{user.age.min}") @Max(value = 150, message = "{user.age.max}") Integer age
) {
}
