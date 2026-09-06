# Java 25モダン構文実践(record/sealed/パターンマッチング)

## 学習ポイント
record、sealed interface、switchのパターンマッチング(recordパターンによる分解)、text block

## 概要
注文の配送状況を管理する対話式CLIツール。
`place <注文ID>` / `ship <注文ID> <伝票番号>` / `deliver <注文ID>` / `cancel <注文ID> <理由>` /
`status <注文ID>` / `list` / `exit`。

## 実装メモ
- 注文の状態を`sealed interface`(`OrderState`)+`record`(`Placed`/`Shipped`/`Delivered`/`Cancelled`)でモデリングした。`permits`で取りうる状態を4種類に限定しているため、`OrderStateTransition`/`OrderReceiptFormatter`のswitch式は`default`節なしでコンパイラに網羅性を保証させている。
- `OrderStateTransition`はswitchのrecordパターン(`case OrderState.Shipped(var orderedDate, var trackingNumber, var shippedDate) -> ...`)で現在の状態を分解しつつ、許可された遷移元かどうかを判定する。`Placed`からのみ`ship`可能、`Shipped`からのみ`deliver`可能、`Delivered`後は`cancel`不可、といった状態機械のルールをswitch式1つに集約した。
- `OrderReceiptFormatter`はtext blockでレシートのレイアウトを組み立て、状態ごとに埋め込む項目(伝票番号・配達日・キャンセル理由など)をswitchパターンマッチングで切り替える。
- `Shipped`の伝票番号・`Cancelled`の理由はrecordのコンパクトコンストラクタで空文字チェックを行い、不正な状態オブジェクトがそもそも生成できないようにした。
- `Main`は01〜30と同じ設計パターン(`Scanner`/`PrintStream`を引数に取る`run`静的メソッド)を踏襲しつつ、日付を`Supplier<LocalDate>`として注入可能にし、テストから日付を固定できるようにした。
- 外部リソース(ネットワーク・ファイル・DB)に依存しない純粋なユニットテストのみで構成した。

## テスト
```bash
cd 31_ModernJavaSyntax
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
