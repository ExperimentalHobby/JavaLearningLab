package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ProductService#fulfillOrder(List)}の{@code @Transactional}によるロールバックを検証する。
 * このテストクラスにはあえて{@code @Transactional}を付けない。付けてしまうと、
 * fulfillOrder内の例外がテスト全体のトランザクション(参加トランザクション)を
 * rollback-onlyにマークし、その後の検証用findById呼び出しが正常終了しようとした際に
 * {@code UnexpectedRollbackException}で失敗してしまう。各Serviceメソッド呼び出しを
 * 独立したトランザクションとして実行させ、実際にDBへコミット/ロールバックされた
 * 結果を検証する。
 */
@SpringBootTest
class OrderFulfillmentTest {

    @Autowired
    private ProductService productService;

    @Test
    void fulfillOrder_sufficientStock_decrementsStockForAllLines() {
        ProductResponse pen = productService.create(new ProductRequest("ペン", new BigDecimal("100"), 10));
        ProductResponse notebook = productService.create(new ProductRequest("ノート", new BigDecimal("150"), 5));

        productService.fulfillOrder(List.of(new OrderLine(pen.id(), 3), new OrderLine(notebook.id(), 2)));

        assertEquals(7, productService.findById(pen.id()).stock());
        assertEquals(3, productService.findById(notebook.id()).stock());
    }

    @Test
    void fulfillOrder_insufficientStockOnSecondLine_rollsBackFirstLineToo() {
        ProductResponse pen = productService.create(new ProductRequest("ペン", new BigDecimal("100"), 10));
        ProductResponse notebook = productService.create(new ProductRequest("ノート", new BigDecimal("150"), 1));

        assertThrows(InsufficientStockException.class, () -> productService.fulfillOrder(
                List.of(new OrderLine(pen.id(), 3), new OrderLine(notebook.id(), 5))));

        // penは先に3個引き当てられたはずだが、notebookの在庫不足で例外が起きたため
        // トランザクション全体がロールバックされ、penの在庫も元の10個に戻っているはず。
        assertEquals(10, productService.findById(pen.id()).stock());
        assertEquals(1, productService.findById(notebook.id()).stock());
    }

    @Test
    void fulfillOrder_concurrentRequestsForSameProduct_neverOversellsStock() throws Exception {
        // 在庫10個に対し、2スレッドが同時に6個ずつ引き当てようとする(合計12>10なので両方は成立し得ない)。
        // 「在庫を読む→判定する→減算する」の間に競合対策がないと、両スレッドとも在庫10を読んだまま
        // 判定を通過してしまい、両方成功する(超過販売)ことがある。
        ProductResponse pen = productService.create(new ProductRequest("ペン", new BigDecimal("100"), 10));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        Callable<Boolean> attempt = () -> {
            ready.countDown();
            start.await();
            try {
                productService.fulfillOrder(List.of(new OrderLine(pen.id(), 6)));
                return true;
            } catch (InsufficientStockException e) {
                return false;
            }
        };

        Future<Boolean> future1 = executor.submit(attempt);
        Future<Boolean> future2 = executor.submit(attempt);
        ready.await();
        start.countDown();

        boolean succeeded1 = future1.get(10, TimeUnit.SECONDS);
        boolean succeeded2 = future2.get(10, TimeUnit.SECONDS);
        executor.shutdown();

        int successCount = (succeeded1 ? 1 : 0) + (succeeded2 ? 1 : 0);
        assertEquals(1, successCount, "在庫10に対し6個の引き当てが2件同時に来た場合、成功できるのは1件だけのはず");

        int finalStock = productService.findById(pen.id()).stock();
        assertTrue(finalStock >= 0, "在庫がマイナスになってはならない: " + finalStock);
    }
}
