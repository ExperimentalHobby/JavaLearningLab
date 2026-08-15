package com.javalab.designpatterns.singleton;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link AppLogger} のSingletonパターン実装を検証するテスト。
 * 「常に同一インスタンスが返る」ことと「そのインスタンスが状態(ログ)を共有する」ことの
 * 両方を確認する(前者だけでは、状態を持たないただの共有関数と区別がつかないため)。
 */
class AppLoggerTest {

    @Test
    void getInstanceAlwaysReturnsSameInstanceAndSharesState() {
        // firstでログを記録した内容が、別変数で取得したsecond経由でも参照できることを確認する
        // (firstとsecondが同一インスタンスであることの実質的な証明)。
        AppLogger first = AppLogger.getInstance();
        AppLogger second = AppLogger.getInstance();

        first.log("最初のログ");

        assertSame(first, second);
        assertTrue(second.logs().contains("最初のログ"));
    }
}
