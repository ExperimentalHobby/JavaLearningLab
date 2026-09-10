package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link LowStockHealthIndicator}を検証する。他のテストクラス(結合テスト等)は
 * {@code @Transactional}を使わず実コミットするため、絶対値ではなく「作成前後の差分」で検証する
 * (残留データの影響を受けない設計)。
 */
@SpringBootTest
@Transactional
class LowStockHealthIndicatorTest {

    @Autowired
    private LowStockHealthIndicator indicator;

    @Autowired
    private ProductRepository repository;

    @Test
    void health_status_isUp() {
        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    void health_afterCreatingLowStockProduct_lowStockCountIncreasesByOne() {
        long before = (long) indicator.health().getDetails().get("lowStockCount");

        repository.save(new Product("在庫少ない商品", new BigDecimal("100"), 1));

        long after = (long) indicator.health().getDetails().get("lowStockCount");

        assertEquals(before + 1, after);
    }
}
