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
        @NotBlank(message = "name must not be blank") String name,
        @NotBlank(message = "email must not be blank") @Email(message = "email must be a valid email address") String email,
        @NotNull(message = "age must not be null") @Min(value = 0, message = "age must be >= 0") @Max(value = 150, message = "age must be <= 150") Integer age
) {
}
