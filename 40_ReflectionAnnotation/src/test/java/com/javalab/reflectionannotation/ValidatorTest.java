package com.javalab.reflectionannotation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {

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
        assertEquals("age must be >= 0", violations.get(0).message());
    }

    @Test
    void validate_ageAboveMax_detectsMaxViolation() {
        UserForm form = new UserForm("山田太郎", 200, "yamada@example.com");

        List<ValidationViolation> violations = Validator.validate(form);

        assertEquals(1, violations.size());
        assertEquals("age must be <= 150", violations.get(0).message());
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
