package com.javalab.reflectionannotation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {

    private static final class NotBlankOnNonStringField {
        @NotBlank
        private final int count = 1;
    }

    private static final class MinOnNonNumberField {
        @Min(0)
        private final String name = "x";
    }

    @Test
    void validate_notBlankOnNonStringField_throwsAnnotationMisuseException() {
        // @NotBlankをString以外に付けると!(value instanceof String)が常にtrueになり、
        // 値に関わらず常に違反扱いされていた。これは利用者の入力の問題ではなく、
        // アノテーションの使い方自体が間違っている「設定エラー」として区別する。
        AnnotationMisuseException ex = assertThrows(AnnotationMisuseException.class,
                () -> Validator.validate(new NotBlankOnNonStringField()));

        assertTrue(ex.getMessage().contains("count"));
    }

    @Test
    void validate_minOnNonNumberField_throwsAnnotationMisuseException() {
        // @Min/@MaxをNumber以外に付けるとvalue instanceof Numberがfalseになり、黙って無視されていた。
        AnnotationMisuseException ex = assertThrows(AnnotationMisuseException.class,
                () -> Validator.validate(new MinOnNonNumberField()));

        assertTrue(ex.getMessage().contains("name"));
    }

    @Test
    void validate_nullTarget_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Validator.validate(null));
    }

    private static class BaseForm {
        @NotBlank
        private final String code;

        BaseForm(String code) {
            this.code = code;
        }
    }

    private static final class ChildForm extends BaseForm {
        ChildForm(String code) {
            super(code);
        }
    }

    @Test
    void validate_inheritedField_detectsViolation() {
        // getDeclaredFields()はそのクラス自身が宣言したフィールドしか返さないため、
        // 親クラス(BaseForm)で宣言された@NotBlankフィールドが検証されていなかった。
        List<ValidationViolation> violations = Validator.validate(new ChildForm(""));

        assertEquals(1, violations.size());
        assertEquals("code", violations.get(0).fieldName());
    }

    @Test
    void validate_blankName_detectsNotBlankViolation() {
        UserForm form = new UserForm("", 30, "yamada@example.com");

        List<ValidationViolation> violations = Validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("name", violations.get(0).fieldName());
    }

    @Test
    void validate_nullName_detectsNotBlankViolation() {
        UserForm form = new UserForm(null, 30, "yamada@example.com");

        List<ValidationViolation> violations = Validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("name", violations.get(0).fieldName());
    }

    @Test
    void validate_ageBelowMin_detectsMinViolation() {
        UserForm form = new UserForm("山田太郎", -1, "yamada@example.com");

        List<ValidationViolation> violations = Validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("ageは0以上である必要があります", violations.get(0).message());
    }

    @Test
    void validate_ageAboveMax_detectsMaxViolation() {
        UserForm form = new UserForm("山田太郎", 200, "yamada@example.com");

        List<ValidationViolation> violations = Validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("ageは150以下である必要があります", violations.get(0).message());
    }

    @Test
    void validate_multipleInvalidFields_detectsAllViolations() {
        UserForm form = new UserForm("", 200, "yamada@example.com");

        List<ValidationViolation> violations = Validator.validate(form);

        assertEquals(2, violations.size());
    }

    @Test
    void validate_validForm_returnsNoViolations() {
        UserForm form = new UserForm("山田太郎", 30, "yamada@example.com");

        List<ValidationViolation> violations = Validator.validate(form);

        assertTrue(violations.isEmpty());
    }
}
