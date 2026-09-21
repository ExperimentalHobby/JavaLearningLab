# ファイル整理ツール

## 学習ポイント
java.nio.file、Path/Files操作

## 概要
指定ディレクトリ直下のファイルを拡張子に応じたサブフォルダ(`images`/`text`/`documents`/`others`)に
振り分ける対話式CLIツール。`organize <ディレクトリパス>`(実際に移動) /
`organize --dry-run <ディレクトリパス>`(実行前に移動予定を確認するドライラン) / `exit`。

## 実装メモ
- 学習ポイント通り`java.io.File`ではなく`java.nio.file.Path`/`Files`で実装した。`Files.list`でディレクトリ直下のエントリのみ取得(再帰しない)、`Files.isRegularFile`でファイルのみ対象化、`Files.createDirectories`でカテゴリフォルダ作成、`Files.move`で移動、と一連の操作をすべて`Files`の静的メソッドで完結させた。
- `FileCategorizer`は拡張子→カテゴリの`Map<String, Set<String>>`を持ち、`categoryOf`で判定する。未知の拡張子・拡張子なしのファイルはいずれも`"others"`に分類する。拡張子の大小文字判定は`toLowerCase(Locale.ROOT)`を使い、実行環境のロケール(例: トルコ語ロケールでの"I"→"ı"変換)に結果が左右されないようにしている。
- `organize`の戻り値は`List<Path>`から`OrganizeResult`(record: 移動できたファイル一覧・失敗したファイルとその理由一覧)に変更した。1ファイルの移動失敗(例: カテゴリ名と同名の通常ファイルが存在し`createDirectories`が失敗するケース)で処理全体を中断せず、残りのファイルの処理を継続する。「途中で失敗すると、どこまで進んだか戻り値に残らない」という問題への対応。
- 整理先に同名ファイルが既に存在する場合、以前は`StandardCopyOption.REPLACE_EXISTING`で警告なく上書きしてデータが失われていた。`"name (2).拡張子"`のようにリネーム退避してから移動するよう変更した。
- `organize(Path, boolean dryRun)`で`dryRun=true`を渡すと、実際のディレクトリ作成・ファイル移動を行わず「移動予定」の対応関係のみを`OrganizeResult`として返す。実行前に影響範囲を確認できなかった問題への対応。
- テストは`@TempDir`で実際のファイル・ディレクトリを作成し、`Files.exists`で移動結果を検証する結合テストとして書いた。モックを使わず実ファイルシステムに対してテストすることで、`java.nio.file`のAPIの挙動そのものを確認できる。
- `organize`はサブディレクトリを移動対象にしないことをテストで確認済み(非再帰的な設計)。
- 分類ルール(拡張子→カテゴリ)の設定ファイル(properties/JSON)化は、新規の設定読み込み機構を要する大きめの拡張のため今回は見送り、今後の拡張候補とした。
- `Main`は01〜12と同じ設計パターンで、不正なパス指定時の継続動作と`organize`実行後の結果表示を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
