package com.javalab.reflectionannotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * リフレクションでオブジェクトのフィールドを走査し、{@link NotBlank}/{@link Min}/{@link Max}が
 * 付与されたフィールドを検証する簡易バリデーター。
 */
public final class Validator {

    private Validator() {
    }

    public static List<ValidationViolation> validate(Object target) {
        Objects.requireNonNull(target, "target must not be null");

        List<ValidationViolation> violations = new ArrayList<>();

        for (Field field : allFields(target.getClass())) {
            if (field.isSynthetic()) {
                continue;
            }
            field.setAccessible(true);
            Object value = readValue(field, target);

            if (field.isAnnotationPresent(NotBlank.class)) {
                validateNotBlank(field, value, violations);
            }
            if (field.isAnnotationPresent(Min.class)) {
                validateMin(field, value, violations);
            }
            if (field.isAnnotationPresent(Max.class)) {
                validateMax(field, value, violations);
            }
        }

        return violations;
    }

    // getDeclaredFields()はそのクラス自身が宣言したフィールドしか返さないため、
    // 親クラスを遡って継承フィールドも走査対象に含める。
    private static List<Field> allFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            fields.addAll(List.of(current.getDeclaredFields()));
        }
        return fields;
    }

    private static void validateNotBlank(Field field, Object value, List<ValidationViolation> violations) {
        if (value != null && !(value instanceof String)) {
            // @NotBlankをString以外のフィールドに付けるのはアノテーションの誤用(設定エラー)であり、
            // 入力値の問題(ValidationViolation)とは区別する。
            throw new AnnotationMisuseException(
                    "@NotBlankはString型のフィールドにのみ付与できます: " + field.getName());
        }
        String s = (String) value;
        if (s == null || s.isBlank()) {
            String message = field.getAnnotation(NotBlank.class).message().replace("{field}", field.getName());
            violations.add(new ValidationViolation(field.getName(), message));
        }
    }

    private static void validateMin(Field field, Object value, List<ValidationViolation> violations) {
        if (value != null && !(value instanceof Number)) {
            throw new AnnotationMisuseException(
                    "@MinはNumber型のフィールドにのみ付与できます: " + field.getName());
        }
        Min annotation = field.getAnnotation(Min.class);
        if (value instanceof Number n && n.intValue() < annotation.value()) {
            String message = annotation.message()
                    .replace("{field}", field.getName())
                    .replace("{value}", String.valueOf(annotation.value()));
            violations.add(new ValidationViolation(field.getName(), message));
        }
    }

    private static void validateMax(Field field, Object value, List<ValidationViolation> violations) {
        if (value != null && !(value instanceof Number)) {
            throw new AnnotationMisuseException(
                    "@MaxはNumber型のフィールドにのみ付与できます: " + field.getName());
        }
        Max annotation = field.getAnnotation(Max.class);
        if (value instanceof Number n && n.intValue() > annotation.value()) {
            String message = annotation.message()
                    .replace("{field}", field.getName())
                    .replace("{value}", String.valueOf(annotation.value()));
            violations.add(new ValidationViolation(field.getName(), message));
        }
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
