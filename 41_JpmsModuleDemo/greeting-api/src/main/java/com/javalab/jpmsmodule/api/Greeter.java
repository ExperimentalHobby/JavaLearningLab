package com.javalab.jpmsmodule.api;

/** 挨拶メッセージを生成する契約。実装は別モジュール(greeting-impl)が提供する。 */
public interface Greeter {

    String greet(String name);
}
