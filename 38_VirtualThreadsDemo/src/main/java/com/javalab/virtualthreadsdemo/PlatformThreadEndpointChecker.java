package com.javalab.virtualthreadsdemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * {@link Executors#newFixedThreadPool(int)}による固定プールサイズのPlatform Threadで並行アクセスする実装。
 * URL数がプールサイズを超えると、超えた分は空いたスレッドが出るまで待たされる
 * (=I/O待ちの間もOSスレッドを占有し続けるため、{@link VirtualThreadEndpointChecker}と異なりスループットが
 * プールサイズに律速される)。
 */
public class PlatformThreadEndpointChecker extends AbstractEndpointChecker {

    private final int poolSize;

    public PlatformThreadEndpointChecker(int poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    protected ExecutorService createExecutor() {
        return Executors.newFixedThreadPool(poolSize);
    }
}
