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
- 実際に`mvn test`(スモークテスト、非フォーク・短縮設定)で得られた計測結果は以下の通り(macOS/Windows等マシン依存のため、あくまで参考値):
  ```
  StringConcatBenchmark.plusOperator    (partCount=10)      0.160  us/op
  StringConcatBenchmark.plusOperator    (partCount=100)     3.196  us/op
  StringConcatBenchmark.plusOperator    (partCount=1000)  287.125  us/op
  StringConcatBenchmark.stringBuilder   (partCount=10)      0.122  us/op
  StringConcatBenchmark.stringBuilder   (partCount=100)     0.975  us/op
  StringConcatBenchmark.stringBuilder   (partCount=1000)    9.353  us/op

  StackBenchmark.arrayDeque             (elementCount=10)     0.032  us/op
  StackBenchmark.arrayDeque             (elementCount=100)    0.919  us/op
  StackBenchmark.arrayDeque             (elementCount=1000)   8.130  us/op
  StackBenchmark.legacyStack            (elementCount=10)     0.038  us/op
  StackBenchmark.legacyStack            (elementCount=100)    0.353  us/op
  StackBenchmark.legacyStack            (elementCount=1000)   4.092  us/op
  StackBenchmark.simpleLinkedStack      (elementCount=10)     0.037  us/op
  StackBenchmark.simpleLinkedStack      (elementCount=100)    0.540  us/op
  StackBenchmark.simpleLinkedStack      (elementCount=1000)   8.031  us/op
  ```
  `@Param`でサイズを振ったことで、計算量の違いがはっきり見える。`plusOperator`は10→100→1000で
  0.16→3.2→287us(約20倍/約90倍)と急激に悪化するのに対し、`stringBuilder`は0.12→0.98→9.4us
  とほぼ線形(約8倍/約10倍)にしか増えない。これは`+`演算子が連結のたびに新しい`String`を
  生成する(合計コピー量がO(n²))のに対し、`StringBuilder`は内部バッファへの追記(合計コピー量がO(n))
  であることの直接的な裏付けになっている。
  スタックの比較では、`elementCount=1000`で自前実装(単方向連結リスト、都度`new Node`でヒープ確保)が
  `ArrayDeque`とほぼ同等、同期化されている`java.util.Stack`がむしろ最も速いという結果になった
  (小さいサイズでは差が小さく誤差の影響を受けやすい)。JMHの出力にもある通り「数値だけを鵜呑みにせず、
  なぜそうなるかを追う」ことが重要で、これはスモークテスト(非フォーク)の参考値であり、
  正式な計測には`target/benchmarks.jar`を`@Fork`/`@Warmup`/`@Measurement`の既定値(コードで
  `@Fork(2, warmups=1)`・`@Warmup(3, 500ms)`・`@Measurement(5, 500ms)`に固定済み)で実行する。

### コードレビュー指摘への対応(Issue #175)
- **JMHの実行条件が未指定だった問題**: `StackBenchmark`/`StringConcatBenchmark`に`@Fork(value = 2, warmups = 1)`/`@Warmup(iterations = 3, time = 500ms)`/`@Measurement(iterations = 5, time = 500ms)`をクラスレベルで明示した。JMHの既定値(5フォーク×5回ウォームアップ×5回計測、各1秒)による長時間実行を避けつつ、どの条件で測ったかをコードに残すことで再現性を担保した。
- **`@Param`によるサイズ変化の計測がなかった問題**: 固定の1000件を`@Param({"10", "100", "1000"})`のフィールドに置き換えた。上記「実装メモ」の通り、サイズを振ったことで`+`演算子(O(n²))と`StringBuilder`(O(n))の計算量の違いが数値として直接見えるようになった。
- **`Blackhole`/`@CompilerControl`/`Mode.Throughput`が未使用だった問題**: 各ベンチマークメソッドの戻り値をやめ`Blackhole`引数で明示的に消費するように変更した(標準的なDCE回避手法)。`StringConcatUtil`の2メソッドに`@CompilerControl(DONT_INLINE)`を付与し、インライン化による境界の曖昧化を防いだ。クラスレベルの`@BenchmarkMode`を`{Mode.AverageTime, Mode.Throughput}`にし、平均時間とスループットの両方を計測するようにした。
- **学習テーマ「比較対象が2件のみ」への対応**: `@Param`追加により実装方式×入力サイズの多次元比較になった。

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
mvn test       # 6件全てパス
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
