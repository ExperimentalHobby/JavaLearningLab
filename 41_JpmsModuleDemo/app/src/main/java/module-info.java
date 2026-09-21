/**
 * アプリケーション本体(エントリーポイント)のモジュール。
 * {@code greeting-impl}には{@code requires}しているが、{@code greeting-impl}は何もexportsして
 * いないため、appから実装クラスを型として直接参照することはできない(コンパイルエラーになる。
 * READMEの再現手順を参照)。{@code uses}を宣言することで、{@code ServiceLoader}経由でのみ
 * {@code Greeter}の実装を取得する設計にしている。
 * ({@code requires}を外して{@code uses}だけにする構成も可能だが、`java -m`のようにモジュール
 * システムが自動でサービス提供モジュールを解決・バインドする実行方法でしか機能せず、
 * Maven Surefireのようなツールで実行するテストでは提供モジュールが解決されないことがあるため、
 * ここでは`requires`を残して確実にモジュールグラフへ含めている。)
 */
module com.javalab.jpmsmodule.app {
    requires com.javalab.jpmsmodule.api;
    requires com.javalab.jpmsmodule.impl;
    uses com.javalab.jpmsmodule.api.Greeter;
}
