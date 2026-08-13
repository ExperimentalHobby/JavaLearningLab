# デザインパターン実践集

## 学習ポイント
Singleton/Factory/Observer/Strategyなどをミニアプリで実装

## 概要
4つの代表的なデザインパターンを、それぞれ独立したミニアプリとして実装し、`Main`のCLIから体験できる。
`log <メッセージ>`(Singleton) / `shape <circle|rectangle> <params...>`(Factory) /
`weather <温度>`(Observer) / `pay <creditcard|paypal> <金額>`(Strategy) / `exit`。

## 実装メモ
- パターンごとにパッケージを分離した(`singleton`/`factory`/`observer`/`strategy`)。各パターンの本質的なロジックはフレームワークに依存しない独立クラスとし、外部リソースに依存しない純粋なユニットテストのみで構成した(Issue #17と同様の方針)。
- **Singleton**: `AppLogger`は`static final`フィールドによるeager initializationを採用した。JVMのクラス初期化はスレッドセーフに1度だけ行われるため、`synchronized`やダブルチェックロッキングを使わずシンプルに安全なSingletonを実現できる。
- **Factory**: `ShapeFactory`はタイプ文字列(`"circle"`/`"rectangle"`)から具象`Shape`(`Circle`/`Rectangle`のrecord実装)を生成するSimple Factoryとして実装した。未知のタイプは`IllegalArgumentException`とした。
- **Observer**: `WeatherStation`(Subject)が`WeatherObserver`(Observer)を`subscribe`/`unsubscribe`で管理し、`setTemperature`で登録済み全員に通知する。テストでは`List::add`をメソッド参照で渡す軽量なテスト用Observerを使い、モックフレームワークなしで検証した。
- **Strategy**: `Checkout`(Context)がコンストラクタで`PaymentStrategy`を受け取り、`setStrategy`で実行時に決済方法を切り替えられることをテストで確認した。
- `Main`は各パターンの主要クラスを配線するだけの薄いレイヤーであり、個々のロジックは既にTDDで検証済みだったため、結合テスト(`MainTest`)は実装後に追加する順序になった(通常のRed→Green順とは異なるが、配線ミスがないことを結合テストで別途保証している)。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
