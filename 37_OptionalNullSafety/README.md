# Optional設計とnull安全

## 学習ポイント
Optionalの正しい使い方、API境界でのnull安全設計

## 概要
社員名簿検索を題材にした対話式CLIツール。
`add <ID> <氏名> <メール>` / `find <IDまたはメール>` / `email <ID>` / `domain <ID>` / `exists <ID>` / `list` / `exit`。

## 実装メモ
- `Employee`(record)のフィールドには`Optional`を持たせず、「見つからないかもしれない」ことは検索系メソッドの**戻り値**として`Optional`で表現する設計に徹底した。フィールド・引数に`Optional`を使うのはアンチパターンとされているため、意識的に避けている。
- 一方で、`Employee`のコンストラクタでは`Objects.requireNonNull`で`id`/`name`/`email`のnullを拒否している。`Optional`が「呼び出し側に値が無いかもしれないことを伝える戻り値の型」であるのに対し、`Objects.requireNonNull`は「そもそもnullを許さない」不変条件のチェックであり、役割が異なる(生成された`Employee`インスタンスのフィールドは常にnullであってはならない)。
- `EmployeeService`で`Optional`の主要APIを使い分けて実践した。
  - `describe`: `map` + `orElse` — 例外を投げず既定値で済ませたい場面
  - `emailOf`: `map` + `orElseThrow` — 呼び出し側に必ず対処させたい場面(見つからなければ`EmployeeNotFoundException`)
  - `findByIdOrEmail`: `or` — 1つ目の検索で見つからなければ2つ目の検索にフォールバックする場面
  - `emailDomainOf`: `flatMap` — `findById`(Optional)の結果に、さらにOptionalを返す`extractDomain`を連結する(`map`だと`Optional<Optional<String>>`になってしまう)
  - `findByIdWithEmailDomain`: `filter` — 条件を満たす場合のみ値を残す
  - `findExisting`: `Optional.stream()` — 複数ID候補のうち実在するものだけを`flatMap(Optional::stream)`で集める
- `Main`は`ifPresentOrElse`(`domain`コマンド、中身の有無に応じた分岐)と`ifPresent`(`exists`コマンド、値があるときだけ副作用を起こし、無ければ何もしない)を使い分けた。
- 外部リソース(ネットワーク・ファイル・DB)に依存しない純粋なユニットテストのみで構成した。

### コードレビュー指摘への対応(Issue #169)
- **例外メッセージが英語だった問題**: `EmployeeNotFoundException`のメッセージを日本語化した。
- **`Employee`に検証がなかった問題**: コンパクトコンストラクタで`Objects.requireNonNull`検証を追加した(上記実装メモ参照)。`EmployeeRepository.findByEmail`の`e.email().equals(email)`でNPEになる可能性を、そもそも`email`がnullの`Employee`を作れないようにすることで解消した。
- **同じIDで`add`すると黙って上書きされる問題**: 既存IDなら`IllegalArgumentException`を送出するようにした。
- **学習テーマ「`flatMap`/`filter`/`Optional.stream()`/`ifPresent`の使用例がない」への対応**: 上記実装メモの通り、4つとも実装・使用例を追加した。

## テスト
```bash
cd 37_OptionalNullSafety
mvn test       # 28件全てパス
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
