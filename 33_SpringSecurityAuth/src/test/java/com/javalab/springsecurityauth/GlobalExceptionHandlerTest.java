package com.javalab.springsecurityauth;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link GlobalExceptionHandler} が{@link org.springframework.security.core.AuthenticationException}の
 * サブタイプを広く(パスワード誤り以外も)401へ変換できることを検証するテスト。
 * Springコンテナは使わず、ハンドラーを直接呼び出すプレーンなユニットテストとした。
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAuthenticationException_badCredentials_returnsJapaneseMessage() {
        ErrorResponse response = handler.handleAuthenticationException(new BadCredentialsException("bad"));

        assertEquals("認証に失敗しました", response.message());
    }

    @Test
    void handleAuthenticationException_disabledAccount_returnsJapaneseMessage() {
        // 修正前はAuthControllerがBadCredentialsExceptionしか捕捉しておらず、
        // DisabledException/LockedExceptionは500になっていた。
        ErrorResponse response = handler.handleAuthenticationException(new DisabledException("disabled"));

        assertEquals("認証に失敗しました", response.message());
    }

    @Test
    void handleAuthenticationException_lockedAccount_returnsJapaneseMessage() {
        ErrorResponse response = handler.handleAuthenticationException(new LockedException("locked"));

        assertEquals("認証に失敗しました", response.message());
    }
}
