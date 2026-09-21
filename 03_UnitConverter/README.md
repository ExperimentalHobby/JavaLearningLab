# 単位変換ツール

## 学習ポイント
HashMapを使った変換テーブル管理

## 概要
数値と単位を入力すると変換する対話式CLIツール。長さ(m, km, cm, mm)・重さ(g, kg, mg)・
温度(c, f, k)の3カテゴリに対応する。`"数値 変換元単位 変換先単位"`形式(例: `5 km m`)で
入力し、`exit`で終了する。単位の大文字・小文字は区別しない。

## 実装メモ
- 数値表現は`double`ではなく`BigDecimal`を用いる(`01_Calculator`と同じ方針)。`double`のままだと
  `7 cm cm`が`7.000000000000001`になるなど丸め誤差が生じるため。
- カテゴリを`UnitCategory`インターフェース(`supports(unit)` / `convert(value, from, to)`)として抽象化した。
  長さ・重さのように「単位→基準単位への換算係数」の掛け算/割り算で変換できるカテゴリは
  `FactorBasedUnitCategory`、温度のように係数だけでは変換できず摂氏を経由する変換式が必要なカテゴリは
  `TemperatureUnitCategory`として実装している。
- `UnitConverter`は`List<UnitCategory>`を保持し、`fromUnit`がどのカテゴリに属するかをStream APIで検索する。
  新しいカテゴリ(例: 面積・体積)を追加する際もリストに1つ追加するだけで済む。
- `toUnit`が`fromUnit`と異なるカテゴリの場合は`UnitConverterException`をスローし、意味のない変換(例: kmをgに変換)を防いでいる。
- `Main`は01/02と同じ設計パターンで、Scanner/PrintStreamを引数に取る`run`静的メソッドに分離し、不正入力時の継続動作や正しい変換結果の表示を結合テストで検証できるようにした。結果表示は`stripTrailingZeros().toPlainString()`で整形し、指数表記(`1.0E-6`)にならないようにしている。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
