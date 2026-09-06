package com.javalab.reflectionannotation;

/** {@link Validator}が検出した検証エラー1件分。 */
public record ValidationViolation(String fieldName, String message) {
}
