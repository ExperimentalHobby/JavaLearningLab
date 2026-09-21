# 並行処理デモ(Virtual Threads)

## 学習ポイント
Virtual Threads(Project Loom)、Platform Threadとの比較

## 概要
複数のURLへ同時にHTTPアクセスして疎通確認する対話式CLIツール。
`check <url...>`(Virtual Thread) / `checkPlatform <poolSize> <url...>`(Platform Thread) / `exit`。

## 実装メモ
- `EndpointChecker`インターフェースに対し、`VirtualThreadEndpointChecker`(`Executors.newVirtualThreadPerTaskExecutor()`)と`PlatformThreadEndpointChecker`(`Executors.newFixedThreadPool(n)`)の2実装を用意した。共通処理(タスク投入・結果収集)は`AbstractEndpointChecker`に集約し、サブクラスは`createExecutor()`でスレッドモデルだけを差し替える設計にした。
- テストはJDK内蔵の`com.sun.net.httpserver.HttpServer`で`Thread.sleep`による疑似遅延エンドポイントを複数用意し、実際にHTTPアクセスして検証した(14_HttpClientToolと同じ「実リソースでのテスト」方針)。**サーバー側も`setExecutor(Executors.newCachedThreadPool())`でマルチスレッド化しないと、`HttpServer`はデフォルトでリクエストを1件ずつ直列処理してしまい、クライアント側の並行性を正しく計測できない点にハマった。**
- Virtual Threadは「5エンドポイント×200ms遅延」を700ms未満(逐次なら約1000ms)で完了できることをテストで実証した。Platform Threadもプールサイズが十分(=エンドポイント数)であれば同様に並行実行できる。
- 一方、Platform Threadでプールサイズを小さく(2)すると、「6エンドポイント×150ms遅延」は3巡必要になり450ms以上かかることをテストで実証した。I/O待ち中もOSスレッドを占有し続けるPlatform Threadは、プールサイズがスループットの上限になる。Virtual ThreadはI/O待ち中にOSスレッドを解放するため、この制約を受けない。

### コードレビュー指摘への対応(Issue #170)
- **1件の疎通失敗で全体の結果が失われる問題 + 不正なURLでアプリが落ちる問題**: 同根の問題としてまとめて対応した。`CheckResult`を`15_MultiThreadDownloader`の`DownloadResult`と同じ設計パターン(`success`フラグ+`errorMessage`)に拡張し、`AbstractEndpointChecker.check`内で`IOException`/`InterruptedException`/`IllegalArgumentException`(不正なURL)をすべて捕捉して失敗の`CheckResult`として返すようにした。これにより1件の失敗が他の結果を道連れにすることも、アプリごとクラッシュすることもなくなった。
- **仮想スレッドとプラットフォームスレッドの所要時間を比較していなかった問題**: `Main`で`checkAll`呼び出し全体の経過時間を計測し、「所要時間: xxxms」として表示するようにした。同じURL群に対して`check`と`checkPlatform <小さいpoolSize>`を実行し比較することで、Virtual Threadの並行度の高さを体感できる。
- **`HttpClient`にタイムアウト設定がなかった問題**: `HttpClient.connectTimeout`と`HttpRequest.timeout`に5秒を設定した。
- **学習テーマ「スレッドのピン留め・StructuredTaskScope」への対応**:
  - `StructuredTaskScope`はJava 25時点でもプレビューAPIのままで、有効化には`--enable-preview`がコンパイル・実行の両方に必要。本リポジトリの他フォルダはプレビュー機能を使わない方針のため、1フォルダだけ導入するのはスコープが大きく見送った。
  - スレッドのピン留め(pinning)は概念の解説に留めた: Virtual Threadは通常I/O待ちでキャリア(OS)スレッドを解放するが、`synchronized`ブロックの中でブロッキング処理(I/Oや`Thread.sleep`)を行うと、そのVirtual Threadはキャリアスレッドに「ピン留め」され、解放されなくなる。これは`AbstractEndpointChecker`のようにVirtual Threadを大量に使う設計で、うっかり`synchronized`でHTTP通信をラップしてしまうと、Platform Threadと同じスループットの制約を受けてしまう典型的な落とし穴。確実な再現には`-Djdk.tracePinnedThreads`等の実行時診断が必要でテストとして安定させにくいため、コード上の実装は追加せず解説のみとした。

## テスト
```bash
cd 38_VirtualThreadsDemo
mvn test       # 12件全てパス
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
