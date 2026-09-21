# 銀行口座管理システム(CLI)

## 学習ポイント
クラス設計、カプセル化、例外処理(独自例外クラス)

## 概要
入出金と残高照会・取引履歴表示ができる対話式CLI銀行口座システム。起動時に口座名義人名を入力し、
`deposit <金額>` / `withdraw <金額>` / `balance` / `history` / `exit` コマンドで操作する。

## 実装メモ
- `Account`は`balance`(残高)と`history`(取引履歴)をいずれも`private`で保護し、`deposit`/`withdraw`/`transferTo`メソッドを通じてのみ変更できるようにした。`getHistory()`は`List.copyOf`で変更不可なビューを返すため、呼び出し側が返り値の`List`に`add`しても内部状態には影響しない(カプセル化の実践)。`deposit`/`withdraw`はamountが`null`の場合も`InvalidAmountException`をスローする。
- 独自例外は`BankAccountException`(abstract基底)を`InvalidAmountException`(不正な金額)と`InsufficientBalanceException`(残高不足)がそれぞれ継承する階層にした。`Main`側では基底クラスで一括catchでき、原因ごとに個別のcatch節を増やさずに済む。
- `Transaction`はJavaの`record`として実装し、取引種別・金額・取引後残高・取引日時(`timestamp`)を持つイミュータブルな値オブジェクトにした。`deposit`/`withdraw`のたびに残高更新と同時に履歴へ記録することで、後から任意の時点の残高推移を追跡できる。取引日時は`Account`が持つ`Clock`から取得しており、テストでは固定`Clock`を注入して決定的に検証している。
- 金額は`setScale(2, RoundingMode.HALF_UP)`で通貨として妥当なスケールに正規化してから残高・履歴に反映する(`deposit 1000.5555`のような小数第3位以下を含む入力でも、通貨として不正な値を保持しないようにするため)。表示側(`Main`)は`01_Calculator`と同じ`stripTrailingZeros().toPlainString()`で整形し、`1000.00`のような冗長な表示を避けている。
- 「複数口座+振込」という学習テーマに対応するため、`Account.transferTo(destination, amount)`を追加した。出金側で先に残高を検証してから両方の口座を更新するため、残高不足の場合はどちらの口座も変更されない(2口座をまたぐ不変条件を保つ)。`Main`のCLIを複数口座管理(口座の作成・切り替え等)に対応させるのは、現在の「起動時に1つの口座を作る」設計からの大きな再設計になるため、今回は見送り今後の拡張候補とした。
- `Main`は01〜09と同じ設計パターンで、Scanner/PrintStreamを引数に取る`run`静的メソッドに分離し、不正な金額入力時の継続動作や一連の入出金操作後の残高表示を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
