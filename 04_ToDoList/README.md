# ToDoリスト(CLI)

## 学習ポイント
ArrayList操作、ファイル保存(java.io)

## 概要
タスクを追加・完了・削除・一覧表示できる対話式CLI ToDoリスト。コマンドは`add <説明>` / `done <番号>` /
`remove <番号>` / `list` / `save` / `load` / `exit`。`save`/`load`でファイル(`todo.txt`)に保存・復元できる。

## 実装メモ
- タスクは`ArrayList<Task>`で管理。`complete`/`remove`はインデックス検証(`validateIndex`)を共通化し、範囲外アクセスは`ToDoListException`として扱う。
- ファイル保存は学習ポイント通り`java.nio.file`ではなく`java.io`(`BufferedWriter`+`FileWriter`、`BufferedReader`+`FileReader`)で実装した。1タスク1行、`"[x] 説明"`/`"[ ] 説明"`形式にすることで、保存・読込のラウンドトリップをテストで検証できるようにした。
- `Task.fromFileLine`は`"[x] "`/`"[ ] "`がどちらも4文字であることを利用し、共通のオフセットで説明部分を取り出している。
- `Main`は01〜03と同じ設計パターンで、保存先ファイルも引数として注入可能にし、テストでは一時ディレクトリ(`@TempDir`)を使って実ファイルI/Oを含めて結合テストできるようにした。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
