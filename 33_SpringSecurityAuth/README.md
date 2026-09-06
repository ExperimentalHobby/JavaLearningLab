# 認証・認可付きAPI(Spring Security + JWT)

## 学習ポイント
Spring Security基礎、認証・認可、JWT

## 概要
ログインするとJWTが発行され、そのJWTを`Authorization: Bearer <token>`ヘッダーに付けることで
保護されたエンドポイントにアクセスできるミニAPI。デモ用ユーザーは`alice`/`password`の1件のみ。

- `POST /api/auth/login` — `{"username": "alice", "password": "password"}`を検証し、成功すればJWTを返す(失敗時401)
- `GET /api/secure/hello` — 有効なJWTが必須。成功すれば`Hello, alice`を返す(トークンなし/不正なら401)

## 実装メモ
- `JwtService`はJJWT(`io.jsonwebtoken`)でトークンの発行・検証を行う。有効期限を`Duration`としてコンストラクタで注入可能にし、テストでは極短時間(1ミリ秒)を設定して`Thread.sleep`と組み合わせることで期限切れを確定的に再現した。
- `SecurityConfig`で`/api/auth/**`のみ`permitAll`とし、それ以外は認証必須にした。セッションは`STATELESS`とし、`JwtAuthenticationFilter`を`UsernamePasswordAuthenticationFilter`の前段に追加している。
- `JwtAuthenticationFilter`(`OncePerRequestFilter`)は`Authorization`ヘッダーからBearerトークンを取り出し、`JwtService`で検証してSecurityContextに認証情報を設定する。トークンが無い・不正な場合は何もせず未認証のまま次のフィルターに委譲し、保護対象エンドポイントへのアクセスは認可設定により401になる。
- デフォルトのSpring Securityはフォームログイン用のリダイレクトを行うため、`exceptionHandling().authenticationEntryPoint(...)`でリダイレクトせず401を返すよう明示的に上書きした。これを忘れるとAPIなのに302が返ってしまう。
- `AuthController`は`AuthenticationManager.authenticate(...)`が`BadCredentialsException`をスローすることを利用し、`@ExceptionHandler(BadCredentialsException.class)`で401にマッピングしている。
- `AuthController`/`SecureController`のテストは`@SpringBootTest(webEnvironment = RANDOM_PORT)` + `TestRestTemplate`で実際に埋め込みTomcatへHTTPリクエストを送る結合テストとした(モック・`MockMvc`は使わない、既存Issueと同じ「実リソースでのテスト」方針)。

## テスト
```bash
cd 33_SpringSecurityAuth
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
