# 簡易データベースアプリ(JDBC)

## 学習ポイント
SQLite/MySQL連携、CRUD操作

## 概要
タスク管理(id・タイトル・完了状態)を題材にしたCRUD操作の対話式CLIツール。
`add <タイトル>` / `list` / `update <id> <タイトル>` / `done <id>` / `delete <id>` /
`addBatch <タイトル1>,<タイトル2>,...`(トランザクション+バッチ登録) / `exit`。
DBは`org.xerial:sqlite-jdbc`を使い、SQLiteファイル(未指定時は`tasks.db`、コマンドライン引数で変更可)に永続化する。

## 実装メモ
- サーバー不要でファイル/インメモリで完結するSQLiteを採用した(MySQLと異なり起動中のDBサーバーが不要で、既存exercise(#13〜#15)と同じ「モックなし・実リソースでの結合テスト」方針に自然に合うため)。
- `TaskRepository`はコンストラクタで`Connection`を注入するDI-for-testabilityパターンを踏襲した。テストは`jdbc:sqlite::memory:`の実DBに対して行い、本番は`jdbc:sqlite:<パス>`のファイルDBを使う。
- 個々のCRUDメソッドの失敗は`SQLException`を`TaskRepositoryException`(非チェック例外)に変換して統一し、呼び出し側のcatch節を1つに集約した。`insert`/`updateTitle`はタイトルが空文字・空白のみの場合も同様に`TaskRepositoryException`を投げる。`insert`は生成キーが取得できない(`getGeneratedKeys().next()`が`false`)場合も例外にする。
- 更新(U)が完了フラグの切り替えのみでタイトルを変更できなかったため、`updateTitle(id, newTitle)`を追加した。
- `Main.main`は`SQLException`をthrowせず、DBに接続できない場合はエラーメッセージを表示して終了する。DBファイルパスはコマンドライン引数(`args[0]`)で指定でき、未指定時は`tasks.db`を使う。
- `updateDone`/`updateTitle`/`delete`は`PreparedStatement#executeUpdate`の戻り値(更新/削除された行数)が0かどうかで、対象IDが存在しなかった場合を判別している。
- 「トランザクション・バッチ更新が未収録」という学習テーマに対応するため、`insertAll(List<String> titles)`を追加した。`setAutoCommit(false)` + `addBatch`/`executeBatch` + `commit`でまとめて登録し、途中のタイトルが不正な場合は`rollback()`して1件も登録されないようにしている(全件成功か全件失敗かのいずれかになる)。
- `sqlite-jdbc`はJNIでネイティブライブラリを読み込むため、テスト実行時にJDK 25の「制限付きメソッド」に関する警告(`WARNING: A restricted method in java.lang.System has been called`)が出力される。これはアプリケーションコード側で制御できないライブラリ内部の`System.load`呼び出しに起因するものであり、テスト結果やビルドには影響しない。
- `Main`は01〜15と同じ設計パターンで、`add`/`list`/`update`/`done`/`delete`/`addBatch`コマンドの結果表示、および不正なコマンド入力時にエラー表示してループを継続する動作を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
