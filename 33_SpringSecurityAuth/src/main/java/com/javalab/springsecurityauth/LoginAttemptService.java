package com.javalab.springsecurityauth;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ユーザー名ごとのログイン失敗回数を記録し、一定回数を超えたら一定時間ロックアウトする。
 * ブルートフォース(総当たり)攻撃対策の学習題材。
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);

    private record AttemptState(int failureCount, Instant lockedUntil) {
    }

    private final Map<String, AttemptState> attemptsByUsername = new ConcurrentHashMap<>();
    private final Clock clock;

    public LoginAttemptService() {
        this(Clock.systemUTC());
    }

    public LoginAttemptService(Clock clock) {
        this.clock = clock;
    }

    public void recordFailure(String username) {
        attemptsByUsername.compute(username, (_, current) -> {
            int failureCount = (current == null ? 0 : current.failureCount()) + 1;
            Instant lockedUntil = failureCount >= MAX_ATTEMPTS ? clock.instant().plus(LOCKOUT_DURATION) : null;
            return new AttemptState(failureCount, lockedUntil);
        });
    }

    public void recordSuccess(String username) {
        attemptsByUsername.remove(username);
    }

    public boolean isLocked(String username) {
        AttemptState state = attemptsByUsername.get(username);
        if (state == null || state.lockedUntil() == null) {
            return false;
        }
        if (clock.instant().isBefore(state.lockedUntil())) {
            return true;
        }
        // ロック期間が過ぎていれば自動解除する。
        attemptsByUsername.remove(username);
        return false;
    }
}
