package com.javalab.springsecurityauth;

/** ログイン成功時に返すJWTのレスポンスDTO。 */
public record AuthResponse(String token) {
}
