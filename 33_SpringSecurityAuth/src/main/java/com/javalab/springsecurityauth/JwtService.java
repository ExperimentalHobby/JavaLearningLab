package com.javalab.springsecurityauth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

/**
 * JWTの発行・検証を行うサービス。
 * 署名鍵は{@code app.jwt.secret}(Base64文字列。環境変数{@code JWT_SECRET}で上書き可能)から注入する。
 * こうすることで、アプリを再起動しても(=同じ鍵で新しいインスタンスが作られても)発行済みトークンを検証できる。
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final Duration validity;

    @Autowired
    public JwtService(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.validity-minutes:60}") long validityMinutes) {
        this(base64Secret, Duration.ofMinutes(validityMinutes));
    }

    /**
     * @param base64Secret 署名鍵をBase64エンコードした文字列(HS256のため32バイト以上が必要)
     * @param validity トークンの有効期間。テストで期限切れを再現できるよう外部から注入可能にしている。
     */
    public JwtService(String base64Secret, Duration validity) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
        this.validity = validity;
    }

    /**
     * テスト用に、設定ファイルを使わずランダムな鍵でインスタンスを作る。
     * @param validity トークンの有効期間
     */
    public JwtService(Duration validity) {
        this.key = Jwts.SIG.HS256.key().build();
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
