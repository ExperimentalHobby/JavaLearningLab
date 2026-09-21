package com.javalab.springsecurityauth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * 有効なJWTがなければアクセスできない保護対象エンドポイント。
 * {@code @PreAuthorize}でUSERロールを要求することで、JWTから復元した権限情報が
 * 実際に認可判定へ使われていることを実演する。
 */
@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/hello")
    @PreAuthorize("hasRole('USER')")
    public String hello(Principal principal) {
        return "Hello, " + principal.getName();
    }
}
