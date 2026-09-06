# Optional設計とnull安全

## 学習ポイント
Optionalの正しい使い方、API境界でのnull安全設計

## 概要
社員名簿検索を題材にした対話式CLIツール。
`add <ID> <氏名> <メール>` / `find <IDまたはメール>` / `email <ID>` / `list` / `exit`。

## 実装メモ
- `Employee`(record)のフィールドには`Optional`を持たせず、「見つからないかもしれない」ことは検索系メソッドの**戻り値**として`Optional`で表現する設計に徹底した。フィールド・引数に`Optional`を使うのはアンチパターンとされているため、意識的に避けている。
- `EmployeeService`で`Optional`の主要APIを使い分けて実践した。
  - `describe`: `map` + `orElse` — 例外を投げず既定値で済ませたい場面
  - `emailOf`: `map` + `orElseThrow` — 呼び出し側に必ず対処させたい場面(見つからなければ`EmployeeNotFoundException`)
  - `findByIdOrEmail`: `or` — 1つ目の検索で見つからなければ2つ目の検索にフォールバックする場面
- `Main`は`ifPresentOrElse`を使い、`Optional`の中身の有無に応じた分岐をif文なしで記述した。
- 外部リソース(ネットワーク・ファイル・DB)に依存しない純粋なユニットテストのみで構成した。

## テスト
```bash
cd 37_OptionalNullSafety
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
