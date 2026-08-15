# Builderパターン活用アプリ(注文管理システム)

## 学習ポイント
複雑なオブジェクト生成の設計練習

## 概要
必須項目(顧客名・配送先住所・商品明細)とオプション項目(支払方法・ギフトラッピング・メモ)が混在する`Order`(注文)を、静的ネストクラス`Order.Builder`によるフルーエントAPIで段階的に組み立てるコンソールアプリ。

コマンド一覧:
- `start <顧客名> <配送先住所>` — 新しい注文のBuilderを開始する(必須項目)
- `item <商品名> <数量> <単価>` — 商品明細を追加する
- `payment <方法>` — 支払方法を設定する(既定値: 代金引換)
- `wrap on|off` — ギフトラッピングの有無を設定する(既定値: なし)
- `note <メモ>` — 注文メモを設定する(既定値: 空)
- `build` — これまでの内容から`Order`を確定し、サマリーを表示する
- `exit` — 終了する

## 実行方法
```bash
cd 26_BuilderPatternOrderApp
mvn compile
mvn exec:java -Dexec.mainClass="com.javalab.builderorder.Main"
```

## 実装メモ
- `Order`は不変オブジェクトとし、生成は必ず`Order.Builder`経由に限定した。必須項目(顧客名・配送先住所)はBuilderのコンストラクタで受け取り、空文字なら即座に`IllegalArgumentException`で弾くことで「不完全なBuilderが生き残る」状態を防いでいる。
- 商品明細が1件も無い状態での`build()`は`IllegalStateException`とし、REPL側では例外を捕捉してエラーメッセージ表示後も同じBuilderで操作を継続できるようにした(入力途中の内容を失わせない設計)。
- `payment`/`wrap`/`note`はいずれも未指定時の既定値をBuilder側フィールド初期値として持たせ、「オプション項目は指定しなくても妥当な注文が作れる」というBuilderパターンの利点を体現した。
- テストは全て実オブジェクトのみで完結し、モックは使用していない(単純なドメインオブジェクトの構築ロジックのため)。

## テスト
```bash
cd 26_BuilderPatternOrderApp
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
