# リフレクション&カスタムアノテーション

## 学習ポイント
リフレクションAPI、カスタムアノテーションの定義と処理

## 概要
独自バリデーションアノテーション(`@NotBlank`/`@Min`/`@Max`)を定義し、リフレクションでフィールドを
走査してアノテーションに応じた検証を行う簡易バリデーションライブラリ。
`register <氏名> <年齢> <メール>` / `exit`。

## 実装メモ
- `@NotBlank`/`@Min`/`@Max`はいずれも`@Target(ElementType.FIELD)` + `@Retention(RetentionPolicy.RUNTIME)`で定義した。`RetentionPolicy.RUNTIME`を指定しないと実行時にリフレクションでアノテーション情報を取得できず`isAnnotationPresent`が常にfalseになる点が、このテーマの一番の学習ポイントだった。
- `Validator.validate`は`target.getClass().getDeclaredFields()`で全フィールドを取得し、`field.setAccessible(true)`でprivateフィールドにもアクセスできるようにした上で、`field.isAnnotationPresent(...)`でアノテーションの有無を判定し、`field.get(target)`で実際の値を読み取って検証する。
- 32_BeanValidationApiがSpringの`@Valid`+Bean Validationという「フレームワーク任せ」の検証だったのに対し、本課題はアノテーション定義からリフレクションによる走査・検証まで全て自前実装することで、フレームワークが裏で何をしているかを体感する内容にした。
- `UserForm`はデモ・テスト専用の検証対象クラス。`name`/`email`に`@NotBlank`、`age`に`@Min(0)`/`@Max(150)`を付与している。
- 外部リソース(ネットワーク・ファイル・DB)に依存しない純粋なユニットテストのみで構成した。

### コードレビュー指摘への対応(Issue #172)
- **アノテーションの誤用が検出されない問題**: `@NotBlank`をString以外に、`@Min`/`@Max`をNumber以外に付けた場合、値に関わらず常に違反/常に無視という非対称な誤動作をしていた。値の入力ミス(`ValidationViolation`)とは区別し、`AnnotationMisuseException`(非チェック例外)を新設して、アノテーションの使い方自体の誤りを即座に検出するようにした。
- **違反メッセージが英語だった問題 + 学習テーマ「`message()`属性がない」への対応**: `@NotBlank`/`@Min`/`@Max`に`message()`属性を追加し、デフォルトを日本語にした。`{field}`/`{value}`のプレースホルダを`Validator`側で実値に置換する(Bean Validationのメッセージ外部化と同様の考え方)。
- **`getDeclaredFields()`のみで継承フィールドが検証されない問題**: `target.getClass()`から`superclass`を辿りながら各クラスの`getDeclaredFields()`を集約するようにした。
- **`validate(null)`でNPE + 合成フィールドが走査対象になる問題**: `Objects.requireNonNull`による明示的なnullチェック(呼び出し側に分かりやすいメッセージ付きの`NullPointerException`)を追加した。`field.isSynthetic()`のフィールド(カバレッジ計測時の`$jacocoData`など)は走査から除外するようにした。
- **学習テーマ「メソッド/クラスレベルのアノテーション処理、コンパイル時`AnnotationProcessor`」への対応**: `AnnotationProcessor`はビルドプロセス自体への組み込みが必要で、フィールドレベル検証というこのフォルダの主眼から大きく外れるためスコープが大きく見送った。

## テスト
```bash
cd 40_ReflectionAnnotation
mvn test       # 14件全てパス
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
