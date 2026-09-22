package com.javalab.springsecurityauth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 認証エラーを一元的に401 Unauthorizedへ変換するハンドラー。
 * {@code BadCredentialsException}だけでなく、アカウント無効化({@code DisabledException})・
 * ロック({@code LockedException})など{@link AuthenticationException}のサブタイプ全般を対象にする
 * (修正前は{@code AuthController}内で{@code BadCredentialsException}のみ捕捉しており、
 * それ以外の認証エラーは500になっていた)。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException ex) {
        return new ErrorResponse("認証に失敗しました");
    }
}
