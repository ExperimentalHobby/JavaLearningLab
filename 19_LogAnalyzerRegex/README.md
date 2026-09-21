# 正規表現ログ解析ツール

## 学習ポイント
Patternクラス、Matcherでのログ抽出

## 概要
`2026-08-13 10:15:30 [ERROR] メッセージ`形式のログ行を正規表現で解析し、タイムスタンプ・ログレベル・
メッセージを抽出する対話式CLIツール。`load <ファイルパス>`でファイルを読み込み、解析成功件数・
スキップした行の詳細・レベル別の件数集計を表示する。直近の`load`結果に対して
`filterByLevel <レベル>` / `errors`(ERRORのみ) / `filterByPeriod <開始日時> <終了日時>`
(ISO形式、例: `2026-08-13T10:00:00`)で絞り込める。`exit`で終了。

## 実装メモ
- `LogParser`は`Pattern.compile("^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) \[(\w+)\] (.+)$")`でコンパイルした正規表現を`Matcher`でマッチさせ、`group(1)`〜`group(3)`からタイムスタンプ・レベル・メッセージを抽出する。
- 実際のログファイルにはスタックトレースの継続行など形式に合わない行が混ざりうるため、`parseAll`は1行の解析失敗(`LogParseException`)で全体を止めず、その行だけスキップして処理を継続する設計にした。単発の`parse`は形式不正を例外として明確に返す。`"2026-13-45 99:99:99"`のように桁数は正規表現に一致するが実在しない日時は、以前は`DateTimeParseException`がスキップ処理をすり抜けアプリ全体が落ちていたため、`parse`内で`LogParseException`に変換するよう修正した。
- 空の`catch`ブロックでスキップした行の内容・行番号が一切残らない問題があったため、`parseAll`の戻り値を`List<LogEntry>`から`ParseResult`(record: 解析成功した`entries`と、行番号・行内容・理由を持つ`SkippedLine`のリスト`skipped`)に変更した。`Main`はスキップした行の行番号・理由を表示する。
- `Files.readAllLines`で全行をメモリに載せていたため、`Main`は`Files.lines`(Stream)を使うよう変更した。`LogParser.parseAll`に`Stream<String>`を受け取るオーバーロードを追加し、既存の`List<String>`版はこれに委譲する形にしている。
- `countByLevel`は`Stream`の`Collectors.groupingBy` + `Collectors.counting()`でレベルごとの件数を集計する(Issue #12で学んだStream APIの実践的な再利用)。`groupingBy`の既定(`HashMap`)だと表示順が不定だったため、Supplierに`TreeMap::new`を渡してレベル名の昇順で確定させている(`12_StreamApiPractice`と同じ方針)。
- 「レベル絞り込み・期間指定・ERROR抽出といった解析機能がなく件数集計のみ」という学習テーマに対応するため、`filterByLevel`・`filterByPeriod`を追加し、`Main`に対応するコマンドを追加した。
- テストは実際のファイル(`@TempDir`)を使った結合テストとした(既存Issue #13/#18と同じ方針、モックなし)。
- `Main`は01〜18と同じ設計パターンで、`load`コマンドの結果表示、および存在しないファイル・不正なコマンド入力時にエラー表示してループを継続する動作を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
