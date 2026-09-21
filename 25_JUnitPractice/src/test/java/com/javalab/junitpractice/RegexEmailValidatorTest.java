package com.javalab.junitpractice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link RegexEmailValidator} の形式検証ロジックを検証するテスト。
 * {@link org.junit.jupiter.params.ParameterizedTest}の基本形(単一の値源{@code @ValueSource}と、
 * 複数件を返す{@code @MethodSource})を学ぶ題材にしている。
 */
@DisplayName("RegexEmailValidator")
class RegexEmailValidatorTest {

    private final RegexEmailValidator validator = new RegexEmailValidator();

    @DisplayName("不正な形式のメールアドレスはfalseを返す")
    @ParameterizedTest(name = "[{index}] \"{0}\" は不正")
    @ValueSource(strings = {
            "not-an-email",
            "missing-at-sign.com",
            "@no-local-part.com",
            "no-domain@",
            "spaces in@example.com",
            "no-dot@example"
    })
    void isValidReturnsFalseForMalformedEmails(String invalidEmail) {
        assertFalse(validator.isValid(invalidEmail));
    }

    @DisplayName("nullはfalseを返す")
    @org.junit.jupiter.api.Test
    void isValidReturnsFalseForNull() {
        assertFalse(validator.isValid(null));
    }

    @DisplayName("正しい形式のメールアドレスはtrueを返す")
    @ParameterizedTest(name = "[{index}] \"{0}\" は正常")
    @MethodSource("validEmails")
    void isValidReturnsTrueForWellFormedEmails(String validEmail) {
        assertTrue(validator.isValid(validEmail));
    }

    static Stream<String> validEmails() {
        return Stream.of(
                "alice@example.com",
                "bob.smith@example.co.jp",
                "carol+tag@example.org");
    }
}
