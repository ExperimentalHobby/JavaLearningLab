package com.javalab.loggingtool;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class LogLevelControllerTest {

    private final LogLevelController controller = new LogLevelController();

    @Test
    void setLevelChangesLoggerLevel() {
        controller.setLevel("com.javalab.loggingtool.testlogger1", Level.ERROR);

        assertEquals(Level.ERROR, controller.getLevel("com.javalab.loggingtool.testlogger1"));
    }

    @Test
    void setLevelSuppressesLogsBelowConfiguredThreshold() {
        String loggerName = "com.javalab.loggingtool.testlogger2";
        Logger logbackLogger = (Logger) LoggerFactory.getLogger(loggerName);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logbackLogger.addAppender(appender);

        controller.setLevel(loggerName, Level.ERROR);
        logbackLogger.info("このINFOログは抑制されるはず");

        assertFalse(appender.list.stream().anyMatch(event -> event.getLevel() == Level.INFO));

        logbackLogger.detachAppender(appender);
    }

    @Test
    void getLevelReturnsNullForLoggerWithoutExplicitLevel() {
        // Logbackのgetter()は「そのロガー自身に明示設定された」レベルのみを返し、
        // 親から継承した実効レベルは含まない(継承分はgetEffectiveLevel()の役割)。
        // この違いを踏まえた上でgetLevel()を提供していることを確認する。
        assertNull(controller.getLevel("com.javalab.loggingtool.testlogger3.neverconfigured"));
    }
}
