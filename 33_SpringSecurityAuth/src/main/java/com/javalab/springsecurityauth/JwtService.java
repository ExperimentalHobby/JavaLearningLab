package com.javalab.springsecurityauth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * JWTの発行・検証を行うサービス。
 * 署名鍵はインスタンスごとに生成する(デモ用途のため再起動すると既存トークンは無効になる)。
 */
@Service
public class JwtService {

    private final SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    private final Duration validity;

    public JwtService() {
        this(Duration.ofHours(1));
    }

    /**
     * @param validity トークンの有効期間。テストで期限切れを再現できるよう外部から注入可能にしている。
     */
    public JwtService(Duration validity) {
        this.validity = validity;
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(validity)))
                .signWith(key)
                .compact();
    }

    /**
     * @throws io.jsonwebtoken.JwtException トークンが不正・期限切れの場合
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
