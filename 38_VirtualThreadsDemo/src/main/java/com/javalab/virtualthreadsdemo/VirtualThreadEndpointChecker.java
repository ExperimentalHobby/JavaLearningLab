package com.javalab.virtualthreadsdemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * {@link Executors#newVirtualThreadPerTaskExecutor()}でURLごとに1つの仮想スレッドを割り当てて
 * 並行アクセスする実装。I/O待ち中は仮想スレッドがOSスレッドを占有しないため、
 * {@link PlatformThreadEndpointChecker}と異なりプールサイズという概念を持たない
 * (URL数がそのまま同時実行数になる)。
 */
public class VirtualThreadEndpointChecker extends AbstractEndpointChecker {

    @Override
    protected ExecutorService createExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
