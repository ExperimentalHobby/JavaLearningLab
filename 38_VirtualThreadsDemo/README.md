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

## テスト
```bash
cd 38_VirtualThreadsDemo
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
