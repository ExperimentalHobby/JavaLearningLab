package com.javalab.jparestapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 在庫が閾値未満の商品数を{@code /actuator/health}へ公開するカスタムヘルスインジケータ。
 * DB接続そのものはSpring Boot標準の{@code db}ヘルスインジケータで既にカバーされるため、
 * ここではビジネスロジックに基づく参考情報として公開する(在庫不足はアプリの死活とは
 * 別軸の情報のため、常にUPとしステータスをDOWNにはしない)。
 * クラス名の「HealthIndicator」を除いた「lowStock」がJSON上のコンポーネント名になる
 * (Spring Bootの命名規則)。
 */
@Component
public class LowStockHealthIndicator implements HealthIndicator {

    private final ProductRepository repository;
    private final int threshold;

    public LowStockHealthIndicator(
            ProductRepository repository, @Value("${app.low-stock-threshold:5}") int threshold) {
        this.repository = repository;
        this.threshold = threshold;
    }

    @Override
    public Health health() {
        long lowStockCount = repository.countByStockLessThan(threshold);
        return Health.up()
                .withDetail("threshold", threshold)
                .withDetail("lowStockCount", lowStockCount)
                .build();
    }
}
