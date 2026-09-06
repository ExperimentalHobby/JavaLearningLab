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

## テスト
```bash
cd 40_ReflectionAnnotation
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
