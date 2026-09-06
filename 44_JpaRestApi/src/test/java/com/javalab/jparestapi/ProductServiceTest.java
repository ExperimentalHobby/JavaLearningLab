package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link ProductService}をSpringコンテナ+実H2に対して検証する。
 * {@code @Transactional}を付与し、各テストメソッドの終了時にDB変更を自動ロールバックすることで
 * テスト間のデータ汚染を防いでいる。
 */
@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void create_thenFindById_returnsRegisteredProduct() {
        ProductResponse created = productService.create(new ProductRequest("ノート", new BigDecimal("150"), 100));

        ProductResponse found = productService.findById(created.id());

        assertEquals("ノート", found.name());
        assertEquals(100, found.stock());
    }

    @Test
    void findById_unknownId_throwsProductNotFoundException() {
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class, () -> productService.findById(999L));

        assertEquals("product not found: id=999", ex.getMessage());
    }

    @Test
    void update_changesNameAndPriceAndStock() {
        ProductResponse created = productService.create(new ProductRequest("ノート", new BigDecimal("150"), 100));

        ProductResponse updated = productService.update(
                created.id(), new ProductRequest("消しゴム", new BigDecimal("80"), 50));

        assertEquals("消しゴム", updated.name());
        assertEquals(new BigDecimal("80"), updated.price());
        assertEquals(50, updated.stock());
    }

    @Test
    void update_unknownId_throwsProductNotFoundException() {
        assertThrows(ProductNotFoundException.class,
                () -> productService.update(999L, new ProductRequest("消しゴム", new BigDecimal("80"), 50)));
    }

    @Test
    void delete_removesProduct() {
        ProductResponse created = productService.create(new ProductRequest("ノート", new BigDecimal("150"), 100));

        productService.delete(created.id());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(created.id()));
    }

    @Test
    void delete_unknownId_throwsProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> productService.delete(999L));
    }
}
