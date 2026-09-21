package com.javalab.springsecurityauth;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * {@code Authorization: Bearer <token>}ヘッダーからJWTを取り出し、有効であれば
 * {@link SecurityContextHolder}に認証情報を設定するフィルター。
 * トークンが無い、または不正な場合は何もせず次のフィルターに委譲する(未認証のまま扱われ、
 * 保護対象エンドポイントへのアクセスは{@link SecurityConfig}の認可設定により401になる)。
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                String username = jwtService.extractUsername(token);
                // JWT自体には権限を持たせず、都度UserDetailsServiceから権限を引き直す。
                // こうすることで、JWTのペイロードを信頼しすぎず(権限変更が即座に反映される)、
                // かつauthoritiesが空になってしまう問題(認可が常に失敗する)を解消できる。
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | UsernameNotFoundException e) {
                // 不正・期限切れトークンや、トークン発行後に削除されたユーザーは認証情報を設定しない。
                // 未認証として扱われ、保護対象エンドポイントへのアクセスはSecurityConfigの認可設定により401になる。
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
