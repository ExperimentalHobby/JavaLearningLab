package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * これまでのDB系テストはSQLite/H2インメモリで済ませていたが、本テストはDockerコンテナで
 * 実際のPostgreSQLを起動し、{@link ProductService}が本物のDBMSに対しても正しく動作することを
 * 検証する。{@code @DynamicPropertySource}でコンテナの接続情報にデータソースを差し替えている。
 */
@SpringBootTest
@Testcontainers
class ProductRepositoryPostgresTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private ProductService productService;

    @Test
    void create_thenFindById_persistsToRealPostgres() {
        ProductResponse created = productService.create(new ProductRequest("ノート", new BigDecimal("150"), 100));

        ProductResponse found = productService.findById(created.id());

        assertEquals("ノート", found.name());
        assertEquals(100, found.stock());
    }

    @Test
    void fulfillOrder_insufficientStock_rollsBackOnRealPostgresToo() {
        ProductResponse pen = productService.create(new ProductRequest("ペン", new BigDecimal("100"), 10));
        ProductResponse notebook = productService.create(new ProductRequest("ノート", new BigDecimal("150"), 1));

        assertThrows(InsufficientStockException.class, () -> productService.fulfillOrder(
                List.of(new OrderLine(pen.id(), 3), new OrderLine(notebook.id(), 5))));

        // 44_JpaRestApiのH2版と同じ検証を、実際のPostgreSQLコンテナに対しても行う。
        assertEquals(10, productService.findById(pen.id()).stock());
        assertEquals(1, productService.findById(notebook.id()).stock());
    }
}
