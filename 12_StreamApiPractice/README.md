# Stream API練習(データ集計ツール)

## 学習ポイント
map/filter/reduce、ラムダ式の活用

## 概要
売上レコード(商品名・カテゴリ・金額・数量)を登録し、Stream APIで集計する対話式CLIツール。
`add <商品名> <カテゴリ> <金額> <数量>` / `list`(一覧・合計・平均・最高額) / `byCategory`(カテゴリ別合計) / `exit`。

## 実装メモ
- `SalesAggregator`はすべて副作用のない静的メソッドとして実装し、`List<SalesRecord>`を受け取って結果を返す設計にした。GUIやCLIから独立しているため、画面表示なしにJUnitでテストできる。
- 合計金額は`map(SalesRecord::amount).reduce(BigDecimal.ZERO, BigDecimal::add)`、合計数量は`mapToInt`+`sum`と、オブジェクト用ストリームとプリミティブ用ストリーム(`IntStream`)の使い分けを実践した。
- カテゴリ別集計は`Collectors.groupingBy`と`Collectors.reducing`を組み合わせ、グルーピングと集約を1つのcollectで完結させた。
- 最高額のレコード取得は`max(Comparator.comparing(SalesRecord::amount))`とラムダ式でComparatorを組み立てる形にした。
- 商品名一覧は`map`(商品名だけ抽出)→`distinct`(重複除去)→`sorted`(ソート)という中間操作の連鎖で実装。日本語の自然順ソートはUnicodeコードポイント順になる(ひらがな→カタカナの順)ことをテストで確認した。
- `Main`は01〜11と同じ設計パターンで、不正な入力時の継続動作と`add`→`list`の一連の操作を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
