package com.javalab.beanvalidationapi;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link UniqueEmail}の検証本体。Springの{@code SpringConstraintValidatorFactory}
 * (spring-boot-starter-validationにより自動設定される)経由で{@link UserService}がDIされるため、
 * リクエストDTOの検証時点で既存データへの重複チェックが行える。
 */
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserService userService;

    public UniqueEmailValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // 空文字・nullは@NotBlank/@Emailの担当なので、ここでは重複判定のみ行う。
        if (email == null || email.isBlank()) {
            return true;
        }
        return !userService.existsByEmail(email);
    }
}
