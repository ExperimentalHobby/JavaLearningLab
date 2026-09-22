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
- 認証エラーの401マッピングは`GlobalExceptionHandler`(`@RestControllerAdvice`)に一元集約している。`AuthenticationManager.authenticate(...)`が投げる`AuthenticationException`のサブタイプ(`BadCredentialsException`/`DisabledException`/`LockedException`等)を広く対象にした。
- `AuthController`/`SecureController`のテストは`@SpringBootTest(webEnvironment = RANDOM_PORT)` + `TestRestTemplate`で実際に埋め込みTomcatへHTTPリクエストを送る結合テストとした(モック・`MockMvc`は使わない、既存Issueと同じ「実リソースでのテスト」方針)。
- JJWT(`io.jsonwebtoken`)は0.12系で`Keys.secretKeyFor(SignatureAlgorithm)`が非推奨になっており、公式移行先の`Jwts.SIG.HS256.key().build()`に置き換えた。
- Spring本体の多くのパッケージ(`org.springframework.web.filter`や`org.springframework.data.repository`等)はパッケージ単位で`@NonNullApi`が宣言されている。これらの型を継承・実装する際、自分のコード側に`@NonNull`を明示しないとVSCode(Eclipse JDT)のnull解析で「継承元は@NonNullを要求しているのに明示されていない」という警告になる。`JwtAuthenticationFilter#doFilterInternal`は`OncePerRequestFilter`のオーバーライドなので各パラメータに`@NonNull`を付与し、`SecurityConfig`の`AbstractHttpConfigurer::disable`のようにSpring側の型で自分から注釈を追加できない箇所は`@SuppressWarnings("null")`で対応した。

### コードレビュー指摘への対応(Issue #167)
- **JWTから権限(ロール)が復元されない問題**: `JwtAuthenticationFilter`が`UsernamePasswordAuthenticationToken(username, null, List.of())`と空の権限で認証オブジェクトを組み立てていたため、`@PreAuthorize`等の認可判定が必ず失敗していた。JWTから取り出したユーザー名で`UserDetailsService.loadUserByUsername`を都度呼び直し、そこで得た権限を使うように修正した。修正の効果を実証するため`SecureController.hello`に`@PreAuthorize("hasRole('USER')")`(`SecurityConfig`に`@EnableMethodSecurity`)を追加した。
- **署名鍵がインスタンス生成時にランダム生成される問題**: `application.properties`の`app.jwt.secret`(Base64。環境変数`JWT_SECRET`で上書き可能)から`JwtService`に注入するようにした。これにより別インスタンス(=アプリ再起動)でも同じ鍵でトークンを検証できる。
- **`@ExceptionHandler(BadCredentialsException.class)`が`AuthController`内に閉じている問題**: `GlobalExceptionHandler`(`@RestControllerAdvice`)に切り出し、`AuthenticationException`全般(`DisabledException`/`LockedException`含む)を401にマッピングするようにした。
- **401のレスポンスボディが空だった問題**: `authenticationEntryPoint`・`GlobalExceptionHandler`の双方で`{"message": "..."}` 形式のJSONボディ(`ErrorResponse`)を返すようにした。
- **資格情報がコードに直書きだった問題**: `application.properties`の`app.demo-user.username`/`app.demo-user.password`(環境変数`DEMO_USER_USERNAME`/`DEMO_USER_PASSWORD`で上書き可能)に切り出した。
- **学習テーマ「ログイン試行回数の制限・リフレッシュトークン・ログアウト」への対応**: このうち「ログイン試行回数の制限」を`LoginAttemptService`(ユーザー名ごとに失敗回数を記録し、5回失敗で15分ロックアウト)として実装した。リフレッシュトークン・ログアウト(トークン失効)は、ステートレスなJWT設計に失効の仕組み(ブラックリストや短命トークン+リフレッシュトークン基盤)を追加する必要がありスコープが大きいため見送った(将来課題)。

## テスト
```bash
cd 33_SpringSecurityAuth
mvn test       # 19件全てパス
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
