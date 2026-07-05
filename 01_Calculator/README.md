# 電卓アプリ(CLI)

## 学習ポイント
四則演算、Scanner入出力、例外処理(ArithmeticExceptionなど)

## 概要
四則演算(+, -, *, /)とメモリ機能(M+, M-, MR, MC)を持つ対話式CLI電卓。
`Scanner`で標準入力から`"数値 演算子 数値"`形式の式を読み取り、計算結果を表示する。`exit`で終了する。

## 実装メモ
- 小数演算の誤差(`double`だと`0.6 - 0.2`が`0.39999999999999997`になる問題)を避けるため、内部の数値表現は`double`ではなく`BigDecimal`を使用した。
- `divide`は無限小数(例: `1/3`)で例外にならないよう、スケール10・`RoundingMode.HALF_UP`で丸めている。ゼロ除算は`BigDecimal`が自動的に`ArithmeticException`をスローする。
- `Calculator`は四則演算メソッド自体は副作用なしのまま、メモリレジスタ(`memory`フィールド)のみ内部状態として持たせる設計にした。
- `Main`はScannerとPrintStreamを引数に取る`run`静的メソッドに分離し、`StringReader`/`ByteArrayOutputStream`を使ってREPLループを結合テストできるようにした。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
