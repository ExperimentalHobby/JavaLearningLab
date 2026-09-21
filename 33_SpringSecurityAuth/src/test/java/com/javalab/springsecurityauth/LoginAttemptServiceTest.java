package com.javalab.springsecurityauth;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ログイン試行回数の制限(一定回数失敗したらロックアウトする)を検証するテスト。
 * 時刻を{@link Clock}経由で注入可能にし、ロックアウトの発生・自動解除をテストから制御できるようにしている。
 */
class LoginAttemptServiceTest {

    private static final int MAX_ATTEMPTS = 5;

    @Test
    void isLocked_beforeMaxAttempts_returnsFalse() {
        LoginAttemptService service = new LoginAttemptService(Clock.systemUTC());

        for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
            service.recordFailure("alice");
        }

        assertFalse(service.isLocked("alice"));
    }

    @Test
    void isLocked_afterMaxAttempts_returnsTrue() {
        LoginAttemptService service = new LoginAttemptService(Clock.systemUTC());

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            service.recordFailure("alice");
        }

        assertTrue(service.isLocked("alice"));
    }

    @Test
    void recordSuccess_resetsFailureCount() {
        LoginAttemptService service = new LoginAttemptService(Clock.systemUTC());
        for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
            service.recordFailure("alice");
        }

        service.recordSuccess("alice");
        service.recordFailure("alice");

        assertFalse(service.isLocked("alice"));
    }

    @Test
    void isLocked_afterLockoutDurationPasses_returnsFalse() {
        Instant base = Instant.parse("2026-01-01T00:00:00Z");
        java.util.concurrent.atomic.AtomicReference<Instant> now = new java.util.concurrent.atomic.AtomicReference<>(base);
        Clock movableClock = new Clock() {
            @Override
            public ZoneOffset getZone() {
                return ZoneOffset.UTC;
            }

            @Override
            public Clock withZone(java.time.ZoneId zone) {
                return this;
            }

            @Override
            public Instant instant() {
                return now.get();
            }
        };
        LoginAttemptService service = new LoginAttemptService(movableClock);
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            service.recordFailure("alice");
        }
        assertTrue(service.isLocked("alice"));

        now.set(base.plus(Duration.ofMinutes(16)));

        assertFalse(service.isLocked("alice"));
    }

    @Test
    void isLocked_unknownUsername_returnsFalse() {
        LoginAttemptService service = new LoginAttemptService(Clock.systemUTC());

        assertFalse(service.isLocked("never-tried"));
    }
}
