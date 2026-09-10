package com.javalab.jpmsmodule.impl;

import com.javalab.jpmsmodule.api.Greeter;
import com.javalab.jpmsmodule.impl.internal.GreetingFormatter;

/** {@link Greeter}の日本語版実装。整形の詳細は非公開パッケージの{@link GreetingFormatter}に委譲する。 */
public class JapaneseGreeter implements Greeter {

    @Override
    public String greet(String name) {
        return GreetingFormatter.format(name);
    }
}
