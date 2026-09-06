package com.javalab.springsecurityauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/** 有効なJWTがなければアクセスできない保護対象エンドポイント。 */
@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/hello")
    public String hello(Principal principal) {
        return "Hello, " + principal.getName();
    }
}
