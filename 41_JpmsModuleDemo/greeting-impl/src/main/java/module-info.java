/**
 * {@code Greeter}のデフォルト実装を提供するモジュール。
 * {@code com.javalab.jpmsmodule.impl.internal}パッケージは意図的にexportsしない
 * (他モジュールからは一切参照できない、JPMSによる強いカプセル化の実演)。
 */
module com.javalab.jpmsmodule.impl {
    requires com.javalab.jpmsmodule.api;

    exports com.javalab.jpmsmodule.impl;
}
