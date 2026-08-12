# クイズアプリ(CLI)

## 学習ポイント
JSON/プロパティファイル読み込み、スコア管理

## 概要
プロパティファイルから問題を読み込んで出題するCLIクイズアプリ。標準入力から回答を1問ずつ受け取り、
正誤フィードバックを表示しながら全問終了後にスコア(正解数・正答率)を表示する。

## 実装メモ
- 問題データは`java.util.Properties`で読み込む形式にした(`question.count`で問題数、`q<N>.question`/`q<N>.answer`で各問を記述)。JSON解析ライブラリの導入は14_HttpClientToolで扱う予定のため、ここでは追加の依存関係が不要なProperties APIを採用した。
- `QuizLoader`は`File`からの読み込み(`loadFromProperties`、テストで実ファイルを使用)と、クラスパスからの読み込み(`loadFromClasspath`、本番実行時にjar内蔵の`questions.properties`を読み込む)を用意し、パース処理自体は`loadFromReader`に共通化して重複を排除した。
- 正解判定(`Question.isCorrect`)は前後の空白除去・大文字小文字の違いを無視することで、ユーザーの些細な入力揺れを許容している。
- `ScoreManager`は出題数0のときに`getPercentage()`がゼロ除算(NaN)にならないようガードしている。
- `Main`は01〜08と同じ設計パターンで、Scanner/PrintStreamを引数に取る`run`静的メソッドに分離し、全問正解・一部不正解それぞれのケースを結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
