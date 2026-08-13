package com.javalab.designpatterns.singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * アプリ全体で共有するログ蓄積先。Singletonパターンのミニアプリ実装。
 * どこから{@link #getInstance()}を呼んでも同一インスタンス・同一状態(ログ)を参照する。
 */
public final class AppLogger {

    // static final フィールドによる eager initialization。
    // クラス初期化はJVMによって1度だけ・スレッドセーフに行われるため、
    // 同期処理なしで安全なSingletonを実現できる。
    private static final AppLogger INSTANCE = new AppLogger();

    private final List<String> logs = new ArrayList<>();

    private AppLogger() {
    }

    public static AppLogger getInstance() {
        return INSTANCE;
    }

    public void log(String message) {
        logs.add(message);
    }

    public List<String> logs() {
        return List.copyOf(logs);
    }
}
