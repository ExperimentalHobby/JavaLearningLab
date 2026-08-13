package com.javalab.designpatterns.singleton;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppLoggerTest {

    @Test
    void getInstanceAlwaysReturnsSameInstanceAndSharesState() {
        AppLogger first = AppLogger.getInstance();
        AppLogger second = AppLogger.getInstance();

        first.log("最初のログ");

        assertSame(first, second);
        assertTrue(second.logs().contains("最初のログ"));
    }
}
