package com.javalab.jpmsmodule.impl.internal;

/**
 * 挨拶メッセージの整形ロジック。{@code impl}モジュール内部の実装詳細であり、
 * このパッケージ({@code com.javalab.jpmsmodule.impl.internal})はmodule-info.javaで
 * exportsしていないため、他モジュール(appなど)からは一切参照できない。
 */
public final class GreetingFormatter {

    private GreetingFormatter() {
    }

    public static String format(String name) {
        return "こんにちは、" + name + "さん!";
    }
}
