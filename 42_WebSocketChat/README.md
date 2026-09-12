# WebSocketチャットアプリ

## 学習ポイント
WebSocket通信、Spring WebSocketまたは標準API(Jakarta WebSocket)

## 概要
Spring WebSocket(`spring-boot-starter-websocket`)によるミニチャットサーバー。
`/chat`エンドポイントに接続し、最初に送ったメッセージがユーザー名として登録される。
以降のメッセージは`<ユーザー名>: <本文>`の形式で全参加者へブロードキャストされ、入退室も通知される。

## 実装メモ
- 24_SocketChatAppは低レベルなTCP Socket通信(バイトストリームを自前でフレーム化)を扱ったが、本課題はWeb標準のWebSocketプロトコル(アプリケーション層でのメッセージフレーム化・ハンドシェイクは全てSpring/Tomcatが処理する)を実践した。`TextWebSocketHandler`を継承するだけで、テキストメッセージの送受信ロジックに集中できる。
- `ChatWebSocketHandler`は`ConcurrentHashMap`でセッションIDとユーザー名を管理する。複数クライアントが同時に接続・切断する可能性があるため、通常の`HashMap`ではなく並行アクセスに安全な`ConcurrentHashMap`を選んだ。
- ブロードキャスト処理では、1つのセッションへの送信が`IOException`で失敗しても他のセッションへの配信を止めないよう、セッションごとに`try-catch`で囲んでログに残すだけにした(1クライアントの接続不良が全体のチャットを止めないようにするため)。
- テストは`@SpringBootTest(webEnvironment = RANDOM_PORT)`で実際に組み込みTomcatを起動し、`StandardWebSocketClient`(Java標準WebSocketクライアントAPI)で実際に`ws://localhost:{port}/chat`へ接続して検証した(モック・擬似的なハンドラ差し替えは使わない、既存Issueと同じ「実リソースでのテスト」方針)。
- 受信メッセージの検証には、`BlockingQueue`にメッセージを積むテスト専用の`RecordingWebSocketHandler`を作成した。サーバーからの配信は非同期に届くため、`Thread.sleep`による待機ではなく`BlockingQueue#poll(timeout)`でタイムアウト付きにポーリングすることで、テストの非決定性(flaky)を避けている。

## 実行方法
```bash
cd 42_WebSocketChat
mvn spring-boot:run
```
起動後、`ws://localhost:8080/chat`へWebSocketクライアント(ブラウザの開発者ツールや`wscat`等)で接続できる。

## テスト
```bash
cd 42_WebSocketChat
mvn test
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
