# ジェネリクス活用ライブラリ(汎用スタック/キュー)

## 学習ポイント
Generic型、型安全なコレクション設計

## 概要
任意の型を格納できる汎用スタック(`GenericStack<T>`)・キュー(`GenericQueue<T>`)と、
境界型パラメータ(`<T extends Comparable<? super T>>`)を使った汎用ユーティリティ
(`CollectionUtils.max`/`min`/`filter`/`map`)を提供するライブラリ。
デモ用に`String`を扱う対話式CLI(`Main`)も用意している。
`stack push/pop/peek/size <値>` / `queue enqueue/dequeue/peek/size <値>` /
`list add <値>` / `list max` / `list min` / `list filter <部分文字列>` / `list map upper` / `exit`。

## 実装メモ
- `java.util.Stack`/`Queue`をラップせず、単方向連結リストで自前実装した。`GenericStack`は`top`のみを持つ単純な連結リスト、`GenericQueue`は`head`/`tail`の両端を管理する連結リストとして設計し、どちらも`private static class Node<T>`をネストしたジェネリッククラスとして定義している。どちらも`Iterable<T>`を実装し、自前の`Iterator`(先頭から順)で拡張forによる走査ができる。`toString()`も先頭から順に要素を並べる形で実装し、デバッグ時に中身を確認しやすくした。
- 空のスタック/キューへの`pop`/`peek`/`dequeue`は、既存exerciseの「カスタム例外で統一する」方針に沿って`EmptyCollectionException`(非チェック例外)に統一した。`CollectionUtils.max`/`min`も空リストの場合は同じ`EmptyCollectionException`をスローするようにし、例外方針をライブラリ全体で統一している(以前は`max`だけ`IllegalArgumentException`だった)。
- `CollectionUtils.max`/`min`は境界型パラメータに`<T extends Comparable<T>>`ではなくPECS(Producer Extends, Consumer Super)に沿った`<T extends Comparable<? super T>>`を使う。`T`自身ではなく`T`の親クラスが`Comparable`を実装している型(継承階層の途中で`Comparable`を実装するケース)でも呼び出せることをテストで確認した。
- `filter(List<T>, Predicate<? super T>)`・`map(List<T>, Function<? super T, ? extends R>)`を追加し、「コレクション2種のみに留まっている」状態を解消した。`Main`の`list`コマンドから`max`/`min`/`filter`/`map`をすべて呼び出せるようにした(以前は実装・テストのある`max`すらCLIから呼べなかった)。
- `Main`は01〜16と同じ設計パターンで、`stack`/`queue`/`list`コマンドの結果表示、および空コレクションへの操作・不正なコマンド入力時にエラー表示してループを継続する動作を結合テストで検証した。`stack push`/`queue enqueue`は値省略時に`ArrayIndexOutOfBoundsException`でクラッシュしていたため、引数の個数を検証してから処理するよう修正した。
- 外部リソース(ネットワーク・ファイル・DB)に依存しない純粋なユニットテストのみで構成した(既存Issue #1〜#12と同様の方針)。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
