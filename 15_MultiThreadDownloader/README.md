# マルチスレッドダウンローダー

## 学習ポイント
Thread/ExecutorService、並行処理の基礎

## 概要
複数のURLを`ExecutorService`で並行ダウンロードし、それぞれ指定先ファイルに保存する対話式CLIツール。
`download <URL1,URL2,...> <保存先ディレクトリ>`(並行ダウンロードし結果表示) / `exit`。

## 実装メモ
- `MultiThreadDownloader.downloadAll`は`Executors.newFixedThreadPool(threadCount)`でダウンロードを並行実行し、`Future`経由でリクエストと同じ順序の結果を回収する。1件の失敗(通信エラー・異常ステータス)は例外を投げず`DownloadResult(success=false)`として記録し、他のダウンロードを止めない設計にした。
- 通信系のテストは実際の外部APIに依存せず、Issue #13/#14と同じ方針でモックも使わず、JDK内蔵の`com.sun.net.httpserver.HttpServer`をephemeralポートで起動する結合テストとした。
- 並行実行を検証するテスト(各エンドポイントに300msの遅延を入れ、3件の合計処理時間が逐次実行より明らかに短いことをアサート)を書いた際、想定外のRedに遭遇した。原因は`MultiThreadDownloader`側ではなく、`HttpServer.create()`のデフォルトexecutorがリクエストを単一スレッドで直列処理してしまう点だった。クライアント側は並行にリクエストを送っていても、サーバー側が直列処理では並行性を正しく検証できないため、テスト用サーバーに`server.setExecutor(Executors.newFixedThreadPool(4))`を設定して解消した。
- `Main`は01〜14と同じ設計パターンで、`download`コマンドの結果表示(成功/失敗を1件ずつ表示)、および不正なコマンド入力時にエラー表示してループを継続する動作を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
