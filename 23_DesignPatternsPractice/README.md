# デザインパターン実践集

## 学習ポイント
Singleton/Factory/Observer/Strategyなどをミニアプリで実装

## 概要
5つの代表的なデザインパターンを、それぞれ独立したミニアプリとして実装し、`Main`のCLIから体験できる。
`log <メッセージ>`(Singleton) / `logs`(Singleton) / `shape <circle|rectangle> <params...>`(Factory) /
`weather <温度>`(Observer) / `weather unsubscribe`(Observer) /
`pay <creditcard|paypal> <金額> [割引率]`(Strategy/Decorator) / `exit`。

## 実装メモ
- パターンごとにパッケージを分離した(`singleton`/`factory`/`observer`/`strategy`)。各パターンの本質的なロジックはフレームワークに依存しない独立クラスとし、外部リソースに依存しない純粋なユニットテストのみで構成した(Issue #17と同様の方針)。
- **Singleton**: `AppLogger`は`static final`フィールドによるeager initializationを採用した。JVMのクラス初期化はスレッドセーフに1度だけ行われるため、`synchronized`やダブルチェックロッキングを使わずシンプルに安全なSingletonを実現できる。
- **Factory**: `ShapeFactory`はタイプ文字列(`"circle"`/`"rectangle"`)から具象`Shape`(`Circle`/`Rectangle`のrecord実装)を生成するSimple Factoryとして実装した。未知のタイプは`IllegalArgumentException`とした。
- **Observer**: `WeatherStation`(Subject)が`WeatherObserver`(Observer)を`subscribe`/`unsubscribe`で管理し、`setTemperature`で登録済み全員に通知する。テストでは`List::add`をメソッド参照で渡す軽量なテスト用Observerを使い、モックフレームワークなしで検証した。
- **Strategy**: `Checkout`(Context)がコンストラクタで`PaymentStrategy`を受け取り、`setStrategy`で実行時に決済方法を切り替えられることをテストで確認した。
- `Main`は各パターンの主要クラスを配線するだけの薄いレイヤーであり、個々のロジックは既にTDDで検証済みだったため、結合テスト(`MainTest`)は実装後に追加する順序になった(通常のRed→Green順とは異なるが、配線ミスがないことを結合テストで別途保証している)。

### コードレビュー指摘への対応(Issue #157)
- **`AppLogger.logs`が非スレッドセーフだった問題**: `ArrayList`から`CopyOnWriteArrayList`に変更した。多数のスレッドから同時に`log()`を呼んでも記録件数が失われないことを確認するテストを追加した(修正前は3回中3回とも失敗する不安定な状態だった)。
- **`ShapeFactory.create`が引数個数を検証しない問題**: 修正前は`shape circle`(半径省略)で`params[0]`が`ArrayIndexOutOfBoundsException`になり「エラー: Index 0 out of bounds for length 0」という英語の内部例外メッセージが表示されていた。図形タイプごとに必要なパラメータ数を検証し、「circleには半径の1個のパラメータが必要です(指定された個数: 0)」のような分かりやすい`IllegalArgumentException`を送出するようにした。
- **記録したログを表示するコマンドがなかった問題**: `logs`コマンドを追加し、`AppLogger.getInstance().logs()`を表示できるようにした。Singletonの眼目である「どこから呼んでも同じインスタンス・同じ状態を参照する」ことを確認できる。
- **`WeatherStation.setTemperature`のCME + unsubscribe手段がなかった問題**: `observers`を直接for-eachで反復していたため、通知中にObserverが自身を`unsubscribe`すると`ConcurrentModificationException`になっていた。反復前に`List.copyOf`でコピーを取るよう修正し、CLIに`weather unsubscribe`コマンドを追加した。
- **学習テーマ「Adapter/Decorator/Template Methodが未収録」への対応**: 3つのうち**Decoratorを実装**した。`decorator.DiscountedPayment`が既存の`PaymentStrategy`をラップし、決済金額に割引を適用してから委譲する。Strategy(アルゴリズムを丸ごと差し替える)とDecorator(既存の実装を合成でラップして振る舞いを追加する)の違いを`pay <方法> <金額> [割引率]`コマンドで対比できる。**Adapter/Template Methodは別途スコープが必要なため見送り**、将来課題とする。

## ステータス
- [ ] 未着手
- [ ] 実装中
- [x] 完成
