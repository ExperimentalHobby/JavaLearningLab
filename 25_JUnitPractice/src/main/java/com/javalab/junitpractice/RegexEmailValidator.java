package com.javalab.junitpractice;

import java.util.regex.Pattern;

/**
 * 簡易な正規表現でメールアドレスの形式を検証する{@link EmailValidator}実装。
 * RFC 5322準拠の完全な検証ではなく、「@が1つあり、ローカル部・ドメイン部にスペースを含まず、
 * ドメイン部にドットがある」という実用上十分な範囲のみをチェックする。
 */
public class RegexEmailValidator implements EmailValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Override
    public boolean isValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
