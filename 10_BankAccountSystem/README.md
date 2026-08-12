# 銀行口座管理システム(CLI)

## 学習ポイント
クラス設計、カプセル化、例外処理(独自例外クラス)

## 概要
入出金と残高照会・取引履歴表示ができる対話式CLI銀行口座システム。起動時に口座名義人名を入力し、
`deposit <金額>` / `withdraw <金額>` / `balance` / `history` / `exit` コマンドで操作する。

## 実装メモ
- `Account`は`balance`(残高)と`history`(取引履歴)をいずれも`private`で保護し、`deposit`/`withdraw`メソッドを通じてのみ変更できるようにした。`getHistory()`は`List.copyOf`で変更不可なビューを返すため、呼び出し側が返り値の`List`に`add`しても内部状態には影響しない(カプセル化の実践)。
- 独自例外は`BankAccountException`(abstract基底)を`InvalidAmountException`(不正な金額)と`InsufficientBalanceException`(残高不足)がそれぞれ継承する階層にした。`Main`側では基底クラスで一括catchでき、原因ごとに個別のcatch節を増やさずに済む。
- `Transaction`はJavaの`record`として実装し、取引種別・金額・取引後残高を持つイミュータブルな値オブジェクトにした。`deposit`/`withdraw`のたびに残高更新と同時に履歴へ記録することで、後から任意の時点の残高推移を追跡できる。
- `Main`は01〜09と同じ設計パターンで、Scanner/PrintStreamを引数に取る`run`静的メソッドに分離し、不正な金額入力時の継続動作や一連の入出金操作後の残高表示を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
