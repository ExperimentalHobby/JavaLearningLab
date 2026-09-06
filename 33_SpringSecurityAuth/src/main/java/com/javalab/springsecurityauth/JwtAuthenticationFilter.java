package com.javalab.springsecurityauth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * {@code Authorization: Bearer <token>}ヘッダーからJWTを取り出し、有効であれば
 * {@link SecurityContextHolder}に認証情報を設定するフィルター。
 * トークンが無い、または不正な場合は何もせず次のフィルターに委譲する(未認証のまま扱われ、
 * 保護対象エンドポイントへのアクセスは{@link SecurityConfig}の認可設定により401になる)。
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                String username = jwtService.extractUsername(token);
                var authentication = new UsernamePasswordAuthenticationToken(username, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException e) {
                // 不正・期限切れトークンは認証情報を設定しない。未認証として扱われ、
                // 保護対象エンドポイントへのアクセスはSecurityConfigの認可設定により401になる。
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
