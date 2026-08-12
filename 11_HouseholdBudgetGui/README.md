# 家計簿アプリ(Swing/JavaFX)

## 学習ポイント
GUI基礎、イベントリスナー、レイアウト管理

## 概要
収入・支出を入力し、一覧表示と収支サマリー(収入・支出・残高)を表示するSwing GUIアプリ。
種別(`INCOME`/`EXPENSE`)・カテゴリ・金額を入力して「追加」ボタンを押すと、取引一覧テーブルと
サマリーが即座に更新される。

## 実装メモ
- GUIの描画・実際のボタンクリックといったイベント発火は、追加のUIテストライブラリ(AssertJ-Swing等)なしには自動テストが困難なため、テスト方針を明確に分離した: 収支計算ロジック(`BudgetManager`)とJTable用データモデル(`BudgetTableModel`)はTDDで自動テストし、`BudgetFrame`(実際のウィンドウ・レイアウト・イベントリスナー配線)は実装後に実機で起動して手動確認した。
- `BudgetManager`は`balance`計算やエントリ管理を`BudgetFrame`から独立させることで、画面表示なしにJUnitでテストできるようにした(01〜10で培ったカプセル化パターンをGUIアプリにも適用)。
- `BudgetTableModel`は`AbstractTableModel`を継承しているが、`getRowCount`/`getColumnCount`/`getValueAt`はいずれも画面描画を伴わない純粋なロジックのため、`JTable`を実際に表示せずにテストできた。
- `BudgetFrame`は`BorderLayout`を軸に、北側の入力フォームは`FlowLayout`、南側のサマリーは`GridLayout`と、配置したい内容に応じてレイアウトマネージャを使い分けた。追加ボタンには`ActionListener`をラムダで登録し、クリック時に入力値を読み取って`BudgetManager.addEntry()`を呼び出し、失敗時は`JOptionPane`でエラーダイアログを表示する。
- 手動確認では、実際にWindows上でアプリを起動し、キー操作でフォーム入力→追加→テーブル反映→サマリー更新→入力欄クリアの一連の流れをスクリーンショットで検証した。Swingの`JButton`は既定でSPACEキーが操作キーであり、ENTERキーでは反応しない(ルートペインのデフォルトボタンに設定した場合を除く)という挙動も実機確認で把握した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
