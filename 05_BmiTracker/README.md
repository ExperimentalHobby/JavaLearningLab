# BMI計算機+履歴保存

## 学習ポイント
CSV読み書き(BufferedReader/Writer)

## 概要
身長・体重からBMIを計算し、判定結果とともに履歴をCSVファイルに保存・読込する対話式CLIツール。
コマンドは`add <身長cm> <体重kg>` / `list` / `save` / `load` / `exit`。

## 実装メモ
- BMIは日本肥満学会基準の4区分(低体重/普通体重/肥満(1度)/肥満(2度以上))で判定する。計算ロジック(`BmiCalculator`)を独立させ、`BmiHistory`から呼び出す構成にした。
- 学習ポイント通り、CSV読み書きは`java.nio.file`ではなく`java.io`(`BufferedWriter`+`FileWriter`、`BufferedReader`+`FileReader`)で実装した。1行目にヘッダ行(`date,height_cm,weight_kg,bmi,category`)を書き込み、読込時は最初の1行を読み飛ばす。
- `BmiRecord`に`toCsvLine()`/`fromCsvLine()`を持たせ、レコード1件の変換ロジックを`BmiHistory`のファイルI/Oから分離した。
- `Main`は測定日を`Supplier<LocalDate>`として注入可能にし(01〜04と同じ設計パターン)、テストでは固定日付を渡すことで`add`→`list`→`save`の結果を決定的に検証できるようにした。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
