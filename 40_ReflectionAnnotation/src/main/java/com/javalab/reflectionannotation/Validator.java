package com.javalab.reflectionannotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * リフレクションでオブジェクトのフィールドを走査し、{@link NotBlank}/{@link Min}/{@link Max}が
 * 付与されたフィールドを検証する簡易バリデーター。
 */
public final class Validator {

    private Validator() {
    }

    public static List<ValidationViolation> validate(Object target) {
        List<ValidationViolation> violations = new ArrayList<>();

        for (Field field : target.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = readValue(field, target);

            if (field.isAnnotationPresent(NotBlank.class) && (!(value instanceof String s) || s.isBlank())) {
                violations.add(new ValidationViolation(field.getName(), field.getName() + " must not be blank"));
            }
            if (field.isAnnotationPresent(Min.class)) {
                int min = field.getAnnotation(Min.class).value();
                if (value instanceof Number n && n.intValue() < min) {
                    violations.add(new ValidationViolation(field.getName(), field.getName() + " must be >= " + min));
                }
            }
            if (field.isAnnotationPresent(Max.class)) {
                int max = field.getAnnotation(Max.class).value();
                if (value instanceof Number n && n.intValue() > max) {
                    violations.add(new ValidationViolation(field.getName(), field.getName() + " must be <= " + max));
                }
            }
        }

        return violations;
    }

    private static Object readValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            // setAccessible(true)を直前に呼んでいるため、通常は到達しない。
            throw new IllegalStateException(e);
        }
    }
}
