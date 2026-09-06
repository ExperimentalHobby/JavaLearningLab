package com.javalab.springsecurityauth;

/** ログインリクエストのDTO。 */
public record AuthRequest(String username, String password) {
}
