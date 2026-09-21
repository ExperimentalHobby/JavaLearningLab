# WebSocketチャットアプリ

## 学習ポイント
WebSocket通信、Spring WebSocketまたは標準API(Jakarta WebSocket)

## 概要
Spring WebSocket(`spring-boot-starter-websocket`)によるミニチャットサーバー。
`/chat`エンドポイントに接続し、最初に送ったメッセージがユーザー名として登録される。
以降のメッセージは`<ユーザー名>: <本文>`の形式で全参加者へブロードキャストされ、入退室も通知される。

## 実装メモ
- 24_SocketChatAppは低レベルなTCP Socket通信(バイトストリームを自前でフレーム化)を扱ったが、本課題はWeb標準のWebSocketプロトコル(アプリケーション層でのメッセージフレーム化・ハンドシェイクは全てSpring/Tomcatが処理する)を実践した。`TextWebSocketHandler`を継承するだけで、テキストメッセージの送受信ロジックに集中できる。
- `ChatWebSocketHandler`は`ConcurrentHashMap`でセッションIDとユーザー名を管理する。複数クライアントが同時に接続・切断する可能性があるため、通常の`HashMap`ではなく並行アクセスに安全な`ConcurrentHashMap`を選んだ。ブロードキャストは`usernames`に登録済みのセッションIDだけを対象にすることで、ユーザー名未登録のセッションが他人の入退室通知を受け取らないようにしている。
- ブロードキャスト処理では、1つのセッションへの送信が`IOException`で失敗しても他のセッションへの配信を止めないよう、セッションごとに`try-catch`で囲んでログに残すだけにした(1クライアントの接続不良が全体のチャットを止めないようにするため)。
- テストは`@SpringBootTest(webEnvironment = RANDOM_PORT)`で実際に組み込みTomcatを起動し、`StandardWebSocketClient`(Java標準WebSocketクライアントAPI)で実際に`ws://localhost:{port}/chat`へ接続して検証した(モック・擬似的なハンドラ差し替えは使わない、既存Issueと同じ「実リソースでのテスト」方針)。
- 受信メッセージの検証には、`BlockingQueue`にメッセージを積むテスト専用の`RecordingWebSocketHandler`を作成した。サーバーからの配信は非同期に届くため、`Thread.sleep`による待機ではなく`BlockingQueue#poll(timeout)`でタイムアウト付きにポーリングすることで、テストの非決定性(flaky)を避けている。

## 実行方法
```bash
cd 42_WebSocketChat
mvn spring-boot:run
```
起動後、ブラウザで`http://localhost:8080/`を開くと簡易チャット画面が使える。
`ws://localhost:8080/chat`へWebSocketクライアント(`wscat`等)で直接接続することもできる。

### コードレビュー指摘への対応(Issue #174)
- **同一セッションへの並行送信が安全でなかった問題**: `WebSocketSession#sendMessage`はスレッドセーフではなく、複数の送信元スレッドから同時にブロードキャストされると破損やエラーの原因になっていた。接続確立時にセッションを`ConcurrentWebSocketSessionDecorator`でラップし、以降の送信は常にこのデコレータ経由で行うようにした。
- **クライアント(HTML/JS)が同梱されていなかった問題**: `src/main/resources/static/index.html`に最小限のチャット画面を追加した。Spring Bootのデフォルト静的リソース配信(`classpath:/static/`)によりルートで配信される。
- **ユーザー名未登録セッションにも入退室通知が届く/入室通知が本人にも届いていた問題**: ブロードキャストの対象を「ユーザー名が登録済みのセッション」に限定し、入室通知は登録した本人を除外して送信するようにした。
- **ユーザー名の空文字・重複・長さが検証されていなかった問題**: 空文字・空白のみ、20文字超、既に使用中のユーザー名を拒否するようにした。検証エラーは登録前の本人にのみ送信し(ブロードキャストしない)、再入力を待つ。
- **テストが手薄だった問題**: ユーザー名検証・入室通知の宛先・未登録セッションの除外・最大接続数・メッセージ長制限のテストを追加した。
- **学習テーマ「ハートビート・最大接続数・メッセージ長制限」への対応**: メッセージ長制限(500文字)・最大接続数(既定100、`app.chat.max-connections`で変更可)を実装した。ハートビートは`@Scheduled(fixedRate = 30_000)`で定期的に`PingMessage`を送信する方式で実装した(`@EnableScheduling`が必要)。

## テスト
```bash
cd 42_WebSocketChat
mvn test       # 10件全てパス
```

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
