/**
 * {@code Greeter}のデフォルト実装を{@code ServiceLoader}経由でのみ提供するモジュール。
 * {@code exports}を一切行わず、{@code provides ... with ...}だけを宣言している点がポイント。
 * サービス提供者({@code JapaneseGreeter})はpublicかつpublicな引数なしコンストラクタを持てば十分で、
 * そのクラスが属するパッケージ自体を公開する必要はない。これにより実装クラスへの直接的な型依存を
 * 一切許さない、より強いカプセル化を実現できる({@code com.javalab.jpmsmodule.impl.internal}は
 * 元々exportsしていなかったが、公開の`impl`パッケージ自体も非公開になった)。
 */
module com.javalab.jpmsmodule.impl {
    requires com.javalab.jpmsmodule.api;

    provides com.javalab.jpmsmodule.api.Greeter with com.javalab.jpmsmodule.impl.JapaneseGreeter;
}
