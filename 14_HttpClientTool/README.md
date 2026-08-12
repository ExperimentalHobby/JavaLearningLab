# 簡易HTTPクライアント

## 学習ポイント
HttpClient(Java 11+)、JSON解析(Jackson/Gson)

## 概要
指定URLへHTTP GETを行い、JSONレスポンスを取得・表示する対話式CLIツール。
`fetch <URL>`(生JSON表示) / `fetchUser <URL>`(Userレコードとして解析・表示) / `exit`。

## 実装メモ
- JSON解析ライブラリは学習ポイント通りJacksonとGsonから選択可能とし、`jackson-databind`を採用した(`ObjectMapper.readValue`でJSON文字列を`User`レコードへ直接変換できる簡潔さを優先)。
- HTTP通信・ステータス異常・JSON解析失敗はすべて`HttpClientException`(非チェック例外)に統一し、呼び出し側のcatch節を1つに集約した。
- 通信系のテストは実際の外部APIに依存せず、また既存exerciseの方針通りモックも使わず、JDK内蔵の`com.sun.net.httpserver.HttpServer`をephemeralポートで起動する結合テストとした。
- 接続失敗ケースのテストでは、ローカルで起動直後に停止したサーバーへ接続する方式だとWindows環境でTCP接続拒否のタイミングが不安定になり、テストが無期限にハングする問題が発生した。`HttpClient`に`connectTimeout`を設定しても解消しなかったため、RFC 2606で名前解決が行われないことが保証された予約TLD `.invalid` への接続を試みる方式に変更し、高速かつ決定的にDNS解決失敗(`UnknownHostException`)を再現できるようにした。
- `Main`は01〜13と同じ設計パターンで、`fetchUser`コマンドの結果表示、および接続失敗時にクラッシュせずエラー表示してループを継続する動作を結合テストで検証した。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
