package com.javalab.loggingtool;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * ロガー名を指定して実行時にログレベルを変更・取得する。
 * SLF4Jの薄いAPIでは提供されない、Logback実装固有のログレベル管理機能を切り出したもの。
 */
public class LogLevelController {

    /**
     * 指定ロガーのログレベルを変更する。
     * @param loggerName 対象ロガー名
     * @param level 変更後のレベル
     */
    public void setLevel(String loggerName, Level level) {
        toLogbackLogger(loggerName).setLevel(level);
    }

    /**
     * 指定ロガーの現在のログレベルを取得する。
     * @param loggerName 対象ロガー名
     * @return 現在のレベル
     */
    public Level getLevel(String loggerName) {
        return toLogbackLogger(loggerName).getLevel();
    }

    private Logger toLogbackLogger(String loggerName) {
        return (Logger) LoggerFactory.getLogger(loggerName);
    }
}
