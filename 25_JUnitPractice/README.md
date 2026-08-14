# 単体テスト練習(JUnit)

## 学習ポイント
TDD、Mockitoを使ったモックテスト

## 概要
注文確認メールを送信する`OrderNotificationService`を題材に、Mockitoによるモックテストを実践するミニアプリ。
`order <メールアドレス> <金額>` / `exit`のREPLで、実際にコンソールへメール送信内容を表示するデモを体験できる。

## 実装メモ
- 01〜24では一貫して「実リソースでのテスト(実HTTPサーバー・実DB・実Socket等)」を用いモックを避けてきたが、本課題は学習ポイントとして明示的にMockitoを扱うため、あえてその方針と対比させた。`EmailSender`(実運用ではSMTP/SES等の外部サービスに依存)は実際に送信すると副作用が大きく実リソースでのテストに向かないため、モックが適切な場面の実例になっている。
- `org.mockito:mockito-junit-jupiter`を使い、`@ExtendWith(MockitoExtension.class)` + `@Mock`アノテーションという標準的な書き方で`EmailSender`をモック化した。`verify()`による呼び出し検証(引数一致・回数)、`doThrow().when()`による例外スタブ、`verify(never())`による「呼ばれていないこと」の検証まで、Mockitoの基本APIを一通り実践した。
- 当初`notifyAll(List<Order>)`というメソッド名で実装しようとしたところ、`java.lang.Object`が持つ`notifyAll()`(スレッド同期用の特殊メソッド)と名前が衝突し、コンパイラが引数なしの`Object.notifyAll()`へ解決してしまうという紛らわしいコンパイルエラーに遭遇した。メソッド名を`notifyOrders`に変更することで解消した。`Object`のメソッド名(`wait`/`notify`/`notifyAll`/`equals`/`hashCode`/`toString`等)との衝突は意図しない挙動を招きやすいため、独自メソッド名として避けるべき教訓として記録する。
- Mockitoの内部実装(バイトコード生成)がJDK 25の動的エージェント読み込みに関する警告を出すが、これはMockito自体がJDKの将来的な制限に向けて対応中の既知の警告であり、テスト結果やビルドには影響しない。
- `Main`は01〜24と同じ設計パターンで、`order`コマンドの結果表示、および不正なコマンド入力時にエラー表示してループを継続する動作を結合テストで検証した(この結合テストは`ConsoleEmailSender`の実出力を検証するものであり、モックではなく実際の標準出力ストリームを使っている)。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
