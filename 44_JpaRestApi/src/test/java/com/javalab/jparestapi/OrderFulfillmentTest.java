package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
