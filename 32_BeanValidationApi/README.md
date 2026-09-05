# 入力検証&エラーハンドリング強化(Bean Validation)

## 学習ポイント
Bean Validation(`@NotBlank`/`@Email`/`@Min`/`@Max`)、`@RestControllerAdvice`による例外の一元ハンドリング

## 概要
ユーザー登録を題材にしたインメモリのREST API。

- `POST /api/users` — ユーザー登録(201、リクエストボディ`{"name": "...", "email": "...", "age": 30}`)。検証エラー時は400
- `GET /api/users` — ユーザー一覧取得
- `GET /api/users/{id}` — 単一ユーザー取得(存在しなければ404)

## 実装メモ
- `UserRegistrationRequest`(record)のフィールドにBean Validationアノテーション(`@NotBlank`/`@Email`/`@NotNull`/`@Min`/`@Max`)を直接付与し、`UserController`側は`@Valid @RequestBody`を付けるだけで検証を有効化した。検証はコントローラーに書かず、DTO自身に宣言する設計とした。
- 検証エラー(`MethodArgumentNotValidException`)は個々のコントローラーでは捕捉せず、`GlobalExceptionHandler`(`@RestControllerAdvice`)に一元集約した。`BindingResult`からフィールドごとのエラーを`List<FieldErrorResponse>`に変換し、400 Bad Requestで返す。
- 存在しないIDへのアクセスによる404は、20_SpringBootApiIntroと同じパターンで`UserNotFoundException`に`@ResponseStatus(HttpStatus.NOT_FOUND)`を付与し、Spring MVCに自動マッピングさせている。
- `UserService`はSpringのDIコンテナ(`@Service`)に登録しつつも、テストでは`new UserService()`でSpringコンテナなしにプレーンなJUnitテストとして直接検証できる設計にした(20番と同じ「ロジックをフレームワークから独立させる」パターン)。
- `UserController`のテストは`@SpringBootTest(webEnvironment = RANDOM_PORT)` + `TestRestTemplate`で実際に埋め込みTomcatへHTTPリクエストを送る結合テストとした。`MockMvc`は使わず、既存Issueと同じ「実リソースでのテスト」方針をHTTPレベルでも徹底している。

## テスト
```bash
cd 32_BeanValidationApi
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
