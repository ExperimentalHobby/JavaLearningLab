package com.javalab.springsecurityauth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ログインしてJWTを発行するエンドポイントを提供する。
 * 認証失敗時の例外処理は{@link GlobalExceptionHandler}に一元集約している。
 * {@link LoginAttemptService}によりブルートフォース攻撃対策(規定回数の失敗でロックアウト)も行う。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    public AuthController(
            AuthenticationManager authenticationManager, JwtService jwtService, LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        if (loginAttemptService.isLocked(request.username())) {
            throw new LockedException("ログイン試行回数が上限に達しました");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (AuthenticationException e) {
            loginAttemptService.recordFailure(request.username());
            throw e;
        }

        loginAttemptService.recordSuccess(request.username());
        return new AuthResponse(jwtService.generateToken(request.username()));
    }
}
