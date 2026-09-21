# 家計簿アプリ(Swing/JavaFX)

## 学習ポイント
GUI基礎、イベントリスナー、レイアウト管理

## 概要
収入・支出を入力し、一覧表示と収支サマリー(収入・支出・残高)を表示するSwing GUIアプリ。
種別(コンボボックスは「収入」/「支出」と日本語表示)・カテゴリ・金額・日付(`yyyy-MM-dd`、既定は
当日)を入力して「追加」ボタンを押すと、取引一覧テーブルとサマリーが即座に更新される。
テーブルの行を選択すると「更新」「削除」ボタンでその行を編集・削除できる。「保存」「読込」ボタンで
CSVファイルへの永続化もできる。

## 実装メモ
- GUIの描画・実際のボタンクリックといったイベント発火は、追加のUIテストライブラリ(AssertJ-Swing等)なしには自動テストが困難なため、テスト方針を明確に分離した: 収支計算ロジック(`BudgetManager`)・CSV変換(`BudgetEntry`)・JTable用データモデル(`BudgetTableModel`)・表示名(`EntryType`)はTDDで自動テストし、`BudgetFrame`(実際のウィンドウ・レイアウト・イベントリスナー配線)は実装後にコンパイル確認とコードレビューで検証した(このリポジトリの既存方針通り、実機起動での手動確認が前提)。
- `BudgetManager`は`balance`計算やエントリ管理を`BudgetFrame`から独立させることで、画面表示なしにJUnitでテストできるようにした(01〜10で培ったカプセル化パターンをGUIアプリにも適用)。`addEntry`/`updateEntry`は金額に加えカテゴリの空白チェックも行う。`removeEntry`/`updateEntry`で行の削除・編集ができる。
- `getEntries()`は`List.copyOf`による全件コピーではなく`Collections.unmodifiableList`によるO(1)の参照専用ビューを返す。`BudgetTableModel.getValueAt`/`getRowCount`はセル描画のたびにこれを呼ぶため、件数が増えても重くならないようにした。
- `BudgetManager.saveTo`/`loadFrom`(`05_BmiTracker`と同じ方針でCSV永続化)と`BudgetEntry.toCsvLine`/`fromCsvLine`を追加し、アプリを閉じてもデータが失われないようにした。壊れたCSVの読込時は`BudgetException`に変換してクラッシュを防いでいる。
- `EntryType`に`getDisplayName()`(「収入」/「支出」)を追加し、`JComboBox<EntryType>`にラムダ実装の`ListCellRenderer`を設定することで、列挙子名(英語)ではなく日本語で表示されるようにした。
- `BudgetTableModel`は`AbstractTableModel`を継承しているが、`getRowCount`/`getColumnCount`/`getValueAt`はいずれも画面描画を伴わない純粋なロジックのため、`JTable`を実際に表示せずにテストできた。
- `BudgetFrame`は`BorderLayout`を軸に、北側の入力フォームは`FlowLayout`、南側のサマリーは`GridLayout`と、配置したい内容に応じてレイアウトマネージャを使い分けた。各ボタンには`ActionListener`をラムダで登録し、クリック時に入力値を読み取って`BudgetManager`のメソッドを呼び出し、失敗時は`JOptionPane`でエラーダイアログを表示する。保存・読込は`JFileChooser`でファイルを選択する。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
