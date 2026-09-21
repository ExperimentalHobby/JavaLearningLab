# BMI計算機+履歴保存

## 学習ポイント
CSV読み書き(BufferedReader/Writer)

## 概要
身長・体重からBMIを計算し、判定結果とともに履歴をCSVファイルに保存・読込する対話式CLIツール。
コマンドは`add <身長cm> <体重kg>` / `list` / `list <開始日> <終了日>` / `stats` / `save` / `load` / `exit`。

## 実装メモ
- BMIは日本肥満学会基準の4区分(低体重/普通体重/肥満(1度)/肥満(2度以上))で判定する。計算ロジック(`BmiCalculator`)を独立させ、`BmiHistory`から呼び出す構成にした。`calculate`は身長・体重が0以下の場合`IllegalArgumentException`をスローし、0除算によるBMI=Infinityの誤判定を防いでいる。
- 学習ポイント通り、CSV読み書きは`java.nio.file`ではなく`java.io`(`BufferedWriter`+`FileWriter`、`BufferedReader`+`FileReader`)で実装した。文字コードは`FileWriter(File, Charset)`/`FileReader(File, Charset)`で`UTF_8`を明示している。1行目にヘッダ行(`date,height_cm,weight_kg,bmi,category`)を書き込み、読込時は最初の1行を読み飛ばす。
- `BmiRecord`に`toCsvLine()`/`fromCsvLine()`を持たせ、レコード1件の変換ロジックを`BmiHistory`のファイルI/Oから分離した。`fromCsvLine()`はカラム数不足・日付形式不正を`IllegalArgumentException`に変換し、手編集等で壊れたCSVを`load`してもクラッシュしないようにしている。
- `getRecords()`は内部の`ArrayList`を`List.copyOf()`でコピーして返し、呼び出し側からの変更を防ぐ(`04_ToDoList`と同じ方針)。
- 「トラッカー」としての推移把握のため、`BmiHistory`に`averageBmi()`/`maxRecord()`/`minRecord()`/期間絞り込み(`getRecords(from, to)`)を追加した。`Main`の`list`表示には各記録の前回比(直前記録とのBMI差)を付記し、`stats`コマンドで平均・最大・最小を表示できるようにした。
- CSVのエスケープ処理は未対応。現状`category`は`BmiCalculator.classify()`が返す固定文字列のみで、日付・数値もカンマを含み得ないため具体的な不具合が再現できず、自由入力のメモ欄等を追加する際の課題として残している(YAGNI)。
- `Main`は測定日を`Supplier<LocalDate>`として注入可能にし(01〜04と同じ設計パターン)、テストでは固定日付を渡すことで`add`→`list`→`save`の結果を決定的に検証できるようにした。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
