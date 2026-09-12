# JMHパフォーマンス計測

## 学習ポイント
JMH(Java Microbenchmark Harness)によるベンチマーク計測、パフォーマンス比較

## 概要
JMHを使い、(1)文字列結合方式(`+`演算子 vs `StringBuilder`)、(2)スタック実装(自前実装の単方向連結
リスト vs `ArrayDeque` vs 同期化された`java.util.Stack`)の性能比較を計測するミニプロジェクト。

## 実装メモ
- ベンチマーク対象のロジック(`StringConcatUtil`/`SimpleLinkedStack`)はベンチマークメソッドに直接書かず、独立したクラスとして切り出した。これにより、まずJUnitで「正しさ」を検証してから、JMHで「速さ」を計測するという2段階の構成にできる。
- `jmh-generator-annprocess`を依存関係に追加しただけでは、Maven(`maven-compiler-plugin`)がアノテーションプロセッサを自動検出せず、`META-INF/BenchmarkList`(JMHがベンチマークを発見するためのメタ情報)が生成されない問題にハマった。`maven-compiler-plugin`の`annotationProcessorPaths`に明示的に指定することで解決した。
- `mvn test`にはJMHの`Runner` APIを使った**スモークテスト**を含めた(`BenchmarkSmokeTest`)。`fork(0)`(同一JVM内実行)・`warmupTime`/`measurementTime`を100msに短縮することで、配線(アノテーションプロセッサの生成物・`Runner`からの発見)が壊れていないことを数秒で確認できるようにしている。**計測結果の精度は保証しない**(JMHの警告にもある通り、非フォーク実行はデバッグ目的専用)。実際の精度の高い計測は、`mvn package`でビルドした実行可能jar(`target/benchmarks.jar`)を手動実行して行う設計としている。
- `maven-shade-plugin`でのパッケージング時、`jmh-core`と`jmh-generator-annprocess`が`LICENSE`/`THIRD-PARTY`/`META-INF/MANIFEST.MF`を重複して持つことによる警告が出るが、これはJMHの依存関係同士の既知の重複であり、`ManifestResourceTransformer`が最終的なマニフェスト(`Main-Class`)を正しく上書きするためビルド・実行には影響しない。
- 実際に`mvn test`(スモークテストの短縮設定ではなく、デバッグ用にデフォルト設定で1回試した際)で得られた計測結果は以下の通り(macOS/Windows等マシン依存のため、あくまで参考値):
  ```
  StringConcatBenchmark.plusOperator     352.019  us/op
  StringConcatBenchmark.stringBuilder     12.737  us/op   (約28倍高速)

  StackBenchmark.arrayDeque                8.847  us/op
  StackBenchmark.legacyStack               7.025  us/op
  StackBenchmark.simpleLinkedStack        11.438  us/op
  ```
  文字列結合は`StringBuilder`が`+`演算子より大幅に高速という教科書通りの結果が得られた一方、スタックの比較では自前実装(単方向連結リスト、都度`new Node`でヒープ確保)が標準コレクションより遅く、同期化されているはずの`java.util.Stack`が`ArrayDeque`と大差ない結果になった。JMHの出力にもある通り「数値だけを鵜呑みにせず、なぜそうなるかを追う」ことが重要で、単発実行・非フォークのため参考値に留まる。

## 実行方法
```bash
cd 43_JmhBenchmark
mvn test               # 正しさの検証+JMHスモークテスト(数秒)
mvn clean package       # 実行可能jarのビルド
java -jar target/benchmarks.jar          # 全ベンチマークを実際の精度で計測(数分かかる)
java -jar target/benchmarks.jar -l       # ベンチマーク一覧のみ表示(計測は行わない)
```

## テスト
```bash
cd 43_JmhBenchmark
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
