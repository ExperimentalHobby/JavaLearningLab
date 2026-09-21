# Java 25モダン構文実践(record/sealed/パターンマッチング)

## 学習ポイント
record、sealed interface、switchのパターンマッチング(recordパターンによる分解)、text block、
unnamed pattern variable(`_`)

## 概要
注文の配送状況を管理する対話式CLIツール。
`place <注文ID>` / `ship <注文ID> <伝票番号>` / `deliver <注文ID>` / `cancel <注文ID> <理由>` /
`status <注文ID>` / `list` / `save <ファイルパス>` / `load <ファイルパス>` / `exit`。

## 実装メモ
- 注文の状態を`sealed interface`(`OrderState`)+`record`(`Placed`/`Shipped`/`Delivered`/`Cancelled`)でモデリングした。`permits`で取りうる状態を4種類に限定しているため、`OrderStateTransition`/`OrderReceiptFormatter`のswitch式は`default`節なしでコンパイラに網羅性を保証させている。
- `OrderStateTransition`はswitchのrecordパターン(`case OrderState.Shipped(var orderedDate, var trackingNumber, var shippedDate) -> ...`)で現在の状態を分解しつつ、許可された遷移元かどうかを判定する。`Placed`からのみ`ship`可能、`Shipped`からのみ`deliver`可能、`Delivered`後は`cancel`不可、といった状態機械のルールをswitch式1つに集約した。
- `OrderReceiptFormatter`はtext blockでレシートのレイアウトを組み立て、状態ごとに埋め込む項目(伝票番号・配達日・キャンセル理由など)をswitchパターンマッチングで切り替える。
- `Shipped`の伝票番号・`Cancelled`の理由はrecordのコンパクトコンストラクタで空文字チェックを行い、不正な状態オブジェクトがそもそも生成できないようにした。
- `Main`は01〜30と同じ設計パターン(`Scanner`/`PrintStream`を引数に取る`run`静的メソッド)を踏襲しつつ、日付を`Supplier<LocalDate>`として注入可能にし、テストから日付を固定できるようにした。
- 外部リソース(ネットワーク・ファイル・DB)に依存しない純粋なユニットテストのみで構成した。
- `OrderStateTransition`のswitch式では、遷移不可な分岐(例外を投げるだけの分岐)や分解結果のうち使わない要素をunnamed pattern variable(`_`、Java 22で正式導入)にし、「意図的に未使用」であることを明示してIDEの未使用変数警告を解消した。

### コードレビュー指摘への対応(Issue #165)
- **`place`が既存注文IDを黙って上書きする問題**: `orders`に既に同じIDがあれば`IllegalArgumentException`(「既に存在する注文IDです」)を投げるようにした。配達完了済み・キャンセル済みの注文が`place`で「注文受付」に戻せてしまう不具合を防ぐ。
- **例外メッセージが英語だった問題**: `OrderStateTransition`の状態遷移エラー、`OrderState`のコンパクトコンストラクタの検証エラーを日本語化した。状態遷移エラーは`OrderState.label()`(状態の日本語ラベルを返すdefaultメソッド)を使い、「発送済みの注文は発送できません」のように組み立てている。
- **`Placed`/`Delivered`にコンパクトコンストラクタの検証がなかった問題**: `Shipped`/`Cancelled`と同様に、`orderedDate`/`trackingNumber`/`deliveredDate`のnullチェックを追加した。
- **`list`が注文IDしか出力しない問題**: `OrderState.label()`を使い`ORD-001: 発送済み`のように状態も表示するようにした。
- **学習テーマ「状態の永続化がない」への対応**: `OrderState`に`@JsonTypeInfo`/`@JsonSubTypes`を付与し、Jacksonのポリモーフィックシリアライズでsealed interfaceの4種類のrecordを型情報付きでJSONへ往復できるようにした(`OrderStatePersistence`)。`save <ファイルパス>` / `load <ファイルパス>` コマンドを追加し、`orders`マップをJSONファイルに保存・復元できる。

## テスト
```bash
cd 31_ModernJavaSyntax
mvn test       # 27件全てパス
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
