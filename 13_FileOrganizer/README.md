# ファイル整理ツール

## 学習ポイント
java.nio.file、Path/Files操作

## 概要
指定ディレクトリ直下のファイルを拡張子に応じたサブフォルダ(`images`/`text`/`documents`/`others`)に
振り分ける対話式CLIツール。`organize <ディレクトリパス>` / `exit`。

## 実装メモ
- 学習ポイント通り`java.io.File`ではなく`java.nio.file.Path`/`Files`で実装した。`Files.list`でディレクトリ直下のエントリのみ取得(再帰しない)、`Files.isRegularFile`でファイルのみ対象化、`Files.createDirectories`でカテゴリフォルダ作成、`Files.move`で移動、と一連の操作をすべて`Files`の静的メソッドで完結させた。
- `FileCategorizer`は拡張子→カテゴリの`Map<String, Set<String>>`を持ち、`categoryOf`で判定する。未知の拡張子・拡張子なしのファイルはいずれも`"others"`に分類する。
- テストは`@TempDir`で実際のファイル・ディレクトリを作成し、`Files.exists`で移動結果を検証する結合テストとして書いた。モックを使わず実ファイルシステムに対してテストすることで、`java.nio.file`のAPIの挙動そのものを確認できる。
- `organize`はサブディレクトリを移動対象にしないことをテストで確認済み(非再帰的な設計)。
- `Main`は01〜12と同じ設計パターンで、不正なパス指定時の継続動作と`organize`実行後の結果表示を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
