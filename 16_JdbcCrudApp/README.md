# 簡易データベースアプリ(JDBC)

## 学習ポイント
SQLite/MySQL連携、CRUD操作

## 概要
タスク管理(id・タイトル・完了状態)を題材にしたCRUD操作の対話式CLIツール。
`add <タイトル>` / `list` / `done <id>` / `delete <id>` / `exit`。
DBは`org.xerial:sqlite-jdbc`を使い、SQLiteファイル(`tasks.db`)に永続化する。

## 実装メモ
- サーバー不要でファイル/インメモリで完結するSQLiteを採用した(MySQLと異なり起動中のDBサーバーが不要で、既存exercise(#13〜#15)と同じ「モックなし・実リソースでの結合テスト」方針に自然に合うため)。
- `TaskRepository`はコンストラクタで`Connection`を注入するDI-for-testabilityパターンを踏襲した。テストは`jdbc:sqlite::memory:`の実DBに対して行い、本番は`jdbc:sqlite:tasks.db`のファイルDBを使う。
- 個々のCRUDメソッドの失敗は`SQLException`を`TaskRepositoryException`(非チェック例外)に変換して統一し、呼び出し側のcatch節を1つに集約した。
- `updateDone`/`delete`は`PreparedStatement#executeUpdate`の戻り値(更新/削除された行数)が0かどうかで、対象IDが存在しなかった場合を判別している。
- `sqlite-jdbc`はJNIでネイティブライブラリを読み込むため、テスト実行時にJDK 25の「制限付きメソッド」に関する警告(`WARNING: A restricted method in java.lang.System has been called`)が出力される。これはアプリケーションコード側で制御できないライブラリ内部の`System.load`呼び出しに起因するものであり、テスト結果やビルドには影響しない。
- `Main`は01〜15と同じ設計パターンで、`add`/`list`/`done`/`delete`コマンドの結果表示、および不正なコマンド入力時にエラー表示してループを継続する動作を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
