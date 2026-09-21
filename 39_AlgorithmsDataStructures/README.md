# アルゴリズム/データ構造実践

## 学習ポイント
探索アルゴリズム(BFS/DFS)、ソートアルゴリズムの比較、木構造

## 概要
代表的なソートアルゴリズムの実装比較と、二分探索木(BST)の自前実装を行う対話式CLIツール。
`sort <selection|insertion|quick|merge> <値...>` / `bst insert <値>` / `bst contains <値>` /
`bst inorder` / `bst size` / `exit`。

## 実装メモ
- `SortAlgorithms`は`<T extends Comparable<? super T>>`境界型パラメータの静的メソッド群とし、選択・挿入・クイック(Lomuto分割)・マージの4アルゴリズムを実装した。いずれも入力の`List`を変更せず、ソート済みの新しいリストを返す設計にした(呼び出し側の意図しない副作用を防ぐため)。
- `List.of()`など要素のない`List`リテラルは型引数を推論できず、`Comparable`境界を満たせないコンパイルエラーになることにハマった。`List.<Integer>of()`のように明示的な型引数を書くことで解決した(実装メモとして記録)。
- `BinarySearchTree<T extends Comparable<? super T>>`は自前の二分探索木。通りがけ順(in-order)で辿ると昇順のリストになる性質を利用し、`inOrderTraversal()`のテストでBSTの構築が正しいことを検証した。挿入時に重複値は無視する設計にした。
- **削除(`delete`)はスコープ外とした**。二分探索木の削除は「子が0/1/2個」の3パターンで処理が分岐し、実装コストの割に本課題の学習ポイント(探索・ソート・木構造の基礎)に対する寄与が小さいと判断したため。
- 外部リソース(ネットワーク・ファイル・DB)に依存しない純粋なユニットテストのみで構成した。

### コードレビュー指摘への対応(Issue #171)
- **「ソートアルゴリズム比較」なのに比較する手段がなかった問題**: `SortMetrics`(比較回数・移動回数・所要時間)/`SortResult`(ソート結果+統計)を新設し、`xxxWithMetrics`系メソッドを追加した。既存の`selectionSort`等はシグネチャを変えず、内部で`xxxWithMetrics`を呼んで結果だけ返す薄いラッパーにした。`swaps`(交換)ではなく`moves`(移動)という用語にしたのは、selection/quickは「交換」だがinsertionは「シフト」、mergeは「書き込み」であり、操作の性質が異なるため(統一的な用語として「移動」を使う)。`Main`の`sort`コマンドに比較回数・移動回数・所要時間の表示を追加した。
- **`quickSort`がソート済み入力でO(n²)・StackOverflowのリスクがあった問題**: 分割前にランダムな要素を末尾(ピボット位置)と交換するようにした。入力の並びに依存する最悪ケースをほぼ回避できる標準的な対策。
- **`BinarySearchTree`の`insert`/`contains`/`inOrder`が再帰実装でStackOverflowのリスクがあった問題**: 3メソッドを反復(iterative)実装に書き換えた。木のバランシング自体は行わないため退化(片側に伸びる形)そのものは解消しないが、大量の単調増加挿入でもスタックを消費しないためクラッシュのリスクは解消した。
- **学習テーマ「境界型が`Comparable<T>`」への対応**: `T extends Comparable<? super T>`に変更した(`17_GenericCollectionLib`と同じ修正)。
- **学習テーマ「木の高さ・バランス、各種走査が未実装」への対応**: `height()`(空の木は-1、単一ノードは0)、`preOrderTraversal()`、`postOrderTraversal()`、`levelOrderTraversal()`(キューを使った幅優先走査)を追加した。

## テスト
```bash
cd 39_AlgorithmsDataStructures
mvn test       # 34件全てパス
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
