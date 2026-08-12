# ジェネリクス活用ライブラリ(汎用スタック/キュー)

## 学習ポイント
Generic型、型安全なコレクション設計

## 概要
任意の型を格納できる汎用スタック(`GenericStack<T>`)・キュー(`GenericQueue<T>`)と、
境界型パラメータ(`<T extends Comparable<T>>`)を使った汎用ユーティリティ(`CollectionUtils.max`)を提供するライブラリ。
デモ用に`String`を扱う対話式CLI(`Main`)も用意している。
`stack push/pop/peek/size <値>` / `queue enqueue/dequeue/peek/size <値>` / `exit`。

## 実装メモ
- `java.util.Stack`/`Queue`をラップせず、単方向連結リストで自前実装した。`GenericStack`は`top`のみを持つ単純な連結リスト、`GenericQueue`は`head`/`tail`の両端を管理する連結リストとして設計し、どちらも`private static class Node<T>`をネストしたジェネリッククラスとして定義している。
- 空のスタック/キューへの`pop`/`peek`/`dequeue`は、既存exerciseの「カスタム例外で統一する」方針に沿って`EmptyCollectionException`(非チェック例外)に統一した。
- `CollectionUtils.max`は境界型パラメータ`<T extends Comparable<T>>`を使い、`Integer`・`String`など`Comparable`を実装する任意の型に対して型安全に動作することをテストで確認した。空リストは`IllegalArgumentException`とした。
- `Main`は01〜16と同じ設計パターンで、`stack`/`queue`コマンドの結果表示、および空コレクションへの操作・不正なコマンド入力時にエラー表示してループを継続する動作を結合テストで検証した。
- 外部リソース(ネットワーク・ファイル・DB)に依存しない純粋なユニットテストのみで構成した(既存Issue #1〜#12と同様の方針)。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
