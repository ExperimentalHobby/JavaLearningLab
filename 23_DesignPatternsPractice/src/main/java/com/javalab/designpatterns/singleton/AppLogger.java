package com.javalab.designpatterns.singleton;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * アプリ全体で共有するログ蓄積先。Singletonパターンのミニアプリ実装。
 * どこから{@link #getInstance()}を呼んでも同一インスタンス・同一状態(ログ)を参照する。
 */
public final class AppLogger {

    // static final フィールドによる eager initialization。
    // クラス初期化はJVMによって1度だけ・スレッドセーフに行われるため、
    // 同期処理なしで安全なSingletonを実現できる。
    private static final AppLogger INSTANCE = new AppLogger();

    // Singletonは「どこからでも同じインスタンスを参照する」性質上、複数スレッドから同時に
    // log()が呼ばれる可能性がある。ArrayListは非スレッドセーフで、並行書き込み時に要素の
    // 欠落や例外(ArrayIndexOutOfBoundsException等)を引き起こすため、書き込み時にコピーを
    // 作るCopyOnWriteArrayListを採用した(読み取り主体でログの書き込み頻度は低い用途に適する)。
    private final List<String> logs = new CopyOnWriteArrayList<>();

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
