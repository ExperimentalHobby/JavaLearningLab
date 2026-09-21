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

### コードレビュー指摘への対応(Issue #166)
- **`GlobalExceptionHandler`が`MethodArgumentNotValidException`しか扱っていなかった問題**: `ConstraintViolationException`(`@Validated`によるメソッドパラメータ検証)・`HttpMessageNotReadableException`(壊れたJSONボディ)・`MethodArgumentTypeMismatchException`(パス変数の型不一致)のハンドラーを追加し、いずれも日本語メッセージ+400 Bad Requestで返すようにした。フィールドに紐付かない単一メッセージ用に`ErrorResponse` recordを新設した。
- **検証メッセージがアノテーションにハードコード・英語だった問題**: `src/main/resources/ValidationMessages.properties`にメッセージを外部化し、日本語化した。アノテーション側は`@NotBlank(message = "{user.name.notBlank}")`のように`{key}`形式でプロパティキーを参照する(Bean Validationの標準機能によるメッセージ外部化)。
- **`UserService.findAll`の順序が不定だった問題**: `ConcurrentHashMap`から`ConcurrentSkipListMap`(キー昇順・スレッドセーフ)に変更した。
- **学習テーマ「カスタム制約アノテーションの実装例がない」「メールアドレスの重複チェックがない」への対応**: `@UniqueEmail`(`@Constraint(validatedBy = UniqueEmailValidator.class)`)を新規実装した。`UniqueEmailValidator`はSpringの`SpringConstraintValidatorFactory`(spring-boot-starter-validationが自動設定)経由で`UserService`をコンストラクタインジェクションし、標準アノテーションだけでは書けない「既存データを参照する業務検証」を実演する(`40_ReflectionAnnotation`の自作バリデータとの対比になる)。
- **`GET /api/users/{id}`にIDの正当性検証がなかった問題**: `id`に`@Positive`を付与し(`UserController`に`@Validated`も付与)、`id<=0`のリクエストは404ではなく400を返すようにした(従来は`findById(-1)`がそのまま`UserNotFoundException`になり404になっていた)。

## テスト
```bash
cd 32_BeanValidationApi
mvn test       # 14件全てパス
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
