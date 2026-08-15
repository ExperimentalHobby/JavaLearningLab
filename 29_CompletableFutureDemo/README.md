# 並行処理デモ(CompletableFuture)

## 学習ポイント
非同期処理チェーン、Future/CompletableFutureの活用

## 概要
複数店舗へ並行して価格問い合わせを行い、結果を集約する価格比較コンソールアプリ。`supplyAsync`・`orTimeout`・`exceptionally`・`allOf`・`thenApply`という主要なCompletableFuture APIを一通り実践している。店舗の応答は`Thread.sleep`で遅延をシミュレートする実オブジェクトとし、モックは使用していない。

コマンド一覧:
- `compare <商品名>` — 固定3店舗(正常応答/タイムアウト/失敗をそれぞれ含む構成)へ並行問い合わせし、各店舗の結果一覧と最安値を表示する
- `exit` — 終了する

## 実行方法
```bash
cd 29_CompletableFutureDemo
mvn compile
mvn exec:java -Dexec.mainClass="com.javalab.completablefuturedemo.Main"
```

## 実装メモ
- `PriceComparisonService.compareAsync`は各店舗の問い合わせを`CompletableFuture.supplyAsync`で並行実行し、`orTimeout`でタイムアウトを検出、`exceptionally`で失敗・タイムアウトを`PriceQuote`の`FAILED`/`TIMEOUT`状態に変換することで、1店舗の失敗が全体の失敗に波及しないようにした。最後に`allOf`で全完了を待ち合わせ`thenApply`で結果一覧に集約している。
- `findCheapestAsync`は`compareAsync().thenApply(...)`で素直にチェーンできる設計にした。ただし`Main`のREPLでは、見積もり一覧の表示と最安値表示の両方が必要なため`findCheapestAsync`を再度呼ぶと問い合わせが二重実行されてしまう。そこで最安値選定ロジックを`cheapestOf(List<PriceQuote>)`として純粋関数に切り出し、`compareAsync`の結果に対して同期的に適用することで二重実行を避けた。
- 並行実行の効果は`PriceComparisonServiceTest`で実測時間により検証している(3店舗×200ms遅延でも合計500ms未満で完了することを確認し、逐次実行(600ms以上)との違いを示した)。実際のスレッドで待機させており、モックやタイマー操作は使用していない。
- `join()`でラップされた例外は`CompletionException`でくるまれるため、`findCheapestAsync`の全滅ケースのテストでは`getCause()`から`NoAvailablePriceException`を検証している。

## テスト
```bash
cd 29_CompletableFutureDemo
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
