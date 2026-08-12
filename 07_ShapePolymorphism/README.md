# 継承/ポリモーフィズム練習(図形計算)

## 学習ポイント
abstract class、interface、OOP基礎の総合練習

## 概要
円・長方形・三角形の面積/周囲長を計算する対話式CLIツール。コマンドは`circle <半径>` /
`rectangle <幅> <高さ>` / `triangle <a> <b> <c>` / `list`(登録済み図形と合計面積を表示) / `exit`。

## 実装メモ
- `Measurable`(interface)で「面積・周囲長を計算できる」という契約を定義し、`Shape`(abstract class)がそれを実装した上で、図形共通の`describe()`メソッド(名前・面積・周囲長を組み合わせた説明文字列生成)を提供する構成にした。interfaceは契約の定義、abstract classは共通実装の共有、という役割の違いを意識した設計。
- `Circle`/`Rectangle`/`Triangle`はいずれも`Shape`を継承し、`area()`/`perimeter()`/`getName()`を独自実装。`Main`では`List<Shape>`として一律に扱い、`shape.area()`の呼び出しが実行時の実際の型(Circle/Rectangle/Triangle)に応じて解決されるポリモーフィズムを`ShapeTest`で検証した。
- `Triangle`はヘロンの公式(`s=(a+b+c)/2`, `面積=√(s(s-a)(s-b)(s-c))`)で面積を計算。コンストラクタで三角不等式(2辺の和が残り1辺より大きい)を検証し、成立しない場合は`ShapeException`をスローする。
- `Main`は01〜06と同じ設計パターンで、Scanner/PrintStreamを引数に取る`run`静的メソッドに分離し、不正入力時の継続動作や複数図形登録後の合計面積表示を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
