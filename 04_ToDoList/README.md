# ToDoリスト(CLI)

## 学習ポイント
ArrayList操作、ファイル保存(java.io)

## 概要
タスクを追加・完了・削除・一覧表示できる対話式CLI ToDoリスト。コマンドは`add <説明>` / `done <番号>` /
`remove <番号>` / `list` / `save` / `load` / `exit`。`save`/`load`でファイル(`todo.txt`)に保存・復元できる。

## 実装メモ
- タスクは`ArrayList<Task>`で管理。`complete`/`remove`はインデックス検証(`validateIndex`)を共通化し、範囲外アクセスは`ToDoListException`として扱う。`getTasks()`は内部の`ArrayList`をそのまま返さず`List.copyOf()`で変更不可なコピーを返し、呼び出し側からの`add`/`remove`/`clear`を防いでいる。
- ファイル保存は学習ポイント通り`java.nio.file`ではなく`java.io`(`BufferedWriter`+`FileWriter`、`BufferedReader`+`FileReader`)で実装した。文字コードは(Java 11以降で追加された)`FileWriter(File, Charset)`/`FileReader(File, Charset)`で`StandardCharsets.UTF_8`を明示している。1タスク1行、`"[x] 説明"`/`"[ ] 説明"`形式にすることで、保存・読込のラウンドトリップをテストで検証できるようにした。
- `Task.fromFileLine`は`"[x] "`/`"[ ] "`がどちらも4文字であることを利用し、共通のオフセットで説明部分を取り出している。空行や壊れた行(いずれのプレフィックスも持たない行)は`ToDoListException`をスローし、手編集された`todo.txt`を`load`してもクラッシュしないようにしている。
- 保存形式(`toFileLine`)と画面表示形式(`toDisplayLine`)を別メソッドに分離した。現時点の見た目は同じだが、一方を変更しても他方に影響しない。
- `add()`は説明文に改行(`\n`/`\r`)が含まれる場合に`ToDoListException`をスローする。1行1タスクという保存形式の前提を守るため。
- `Main`は01〜03と同じ設計パターンで、保存先ファイルも引数として注入可能にし、テストでは一時ディレクトリ(`@TempDir`)を使って実ファイルI/Oを含めて結合テストできるようにした。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
