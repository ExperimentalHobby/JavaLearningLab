# マルチスレッドダウンローダー

## 学習ポイント
Thread/ExecutorService、並行処理の基礎

## 概要
複数のURLを`ExecutorService`で並行ダウンロードし、それぞれ指定先ファイルに保存する対話式CLIツール。
`download <URL1,URL2,...> <保存先ディレクトリ>`(並行ダウンロードし結果表示) / `exit`。

## 実装メモ
- `MultiThreadDownloader.downloadAll`は`Executors.newFixedThreadPool(threadCount)`でダウンロードを並行実行し、`Future`経由でリクエストと同じ順序の結果を回収する。1件の失敗(通信エラー・書き込みエラー・不正なURL・異常ステータス)は例外を投げず`DownloadResult(success=false)`として記録し、他のダウンロードを止めない設計にした。スレッド数はURL数がそのまま使われると際限なく増えるため、上限(`MAX_THREAD_COUNT`)でクランプする。
- レスポンスは`HttpResponse.BodyHandlers.ofByteArray()`(全体をメモリに載せる)ではなく`ofFile(Path)`でストリームのまま直接ファイルへ保存し、大きいファイルでの`OutOfMemoryError`を避ける。保存先ディレクトリが存在しない場合、環境によっては`ofFile()`のエラーが通信エラーと見分けの付かない汎用`IOException`になるため、通信を始める前にディレクトリの有無を検証し「ファイルの書き込みに失敗しました」という区別できるメッセージにしている。
- `HttpRequest.newBuilder()`が投げる`IllegalArgumentException`(未対応スキーム等)は`downloadOne`内で捕捉し失敗結果に変換するため、`Future`の外へ例外が伝播しない。`joinResult`でも`InterruptedException`(割り込み状態を復元)と`ExecutionException`を分けて扱う。
- `HttpClient`には`14_HttpClientTool`と同じく接続タイムアウトを、リクエストにはタイムアウトを設定している。
- 進捗表示(件数・完了率)がなく並行実行の様子が見えないという学習テーマに対応するため、`downloadAll`に完了ごとに呼ばれる進捗コールバック(`Consumer<DownloadResult>`)を受け取るオーバーロードを追加し、`Main`から利用している。
- `Main`は異なるURLでもパスの末尾が同じだと保存ファイル名が衝突し後勝ちで上書きされていた問題に対応するため、ファイル名の重複を検出して`"name (2).ext"`のように連番を付ける。不正なURL(`URI.create()`が例外を投げる文字列)もその1件だけエラー表示し、他のURLの処理は継続する。
- 通信系のテストは実際の外部APIに依存せず、Issue #13/#14と同じ方針でモックも使わず、JDK内蔵の`com.sun.net.httpserver.HttpServer`をephemeralポートで起動する結合テストとした。
- 並行実行を検証するテスト(各エンドポイントに300msの遅延を入れ、3件の合計処理時間が逐次実行より明らかに短いことをアサート)を書いた際、想定外のRedに遭遇した。原因は`MultiThreadDownloader`側ではなく、`HttpServer.create()`のデフォルトexecutorがリクエストを単一スレッドで直列処理してしまう点だった。クライアント側は並行にリクエストを送っていても、サーバー側が直列処理では並行性を正しく検証できないため、テスト用サーバーに`server.setExecutor(Executors.newFixedThreadPool(4))`を設定して解消した。
- `Main`は01〜14と同じ設計パターンで、`download`コマンドの結果表示(成功/失敗を1件ずつ表示)、および不正なコマンド入力時にエラー表示してループを継続する動作を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
