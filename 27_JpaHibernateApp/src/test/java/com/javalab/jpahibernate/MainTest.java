package com.javalab.jpahibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("productPU");
        entityManager = entityManagerFactory.createEntityManager();
        repository = new ProductRepository(entityManager);
    }

    @AfterEach
    void tearDown() {
        entityManager.close();
        entityManagerFactory.close();
    }

    @Test
    void runAddsProductAndShowsItInList() {
        Scanner scanner = new Scanner("add ノート 150 100\nlist\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("追加しました"));
        assertTrue(result.contains("ノート"));
    }

    @Test
    void runUpdatesProductAndReflectsChangesInFind() {
        Scanner scanner = new Scanner("add ノート 150 100\nupdate 1 200 80\nfind 1\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("更新しました"));
        assertTrue(result.contains("価格=200"));
        assertTrue(result.contains("在庫=80"));
    }

    @Test
    void runDeletesProductSoThatListNoLongerShowsIt() {
        Scanner scanner = new Scanner("add ノート 150 100\ndelete 1\nlist\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("削除しました"));
        assertTrue(result.contains("商品はありません"));
    }
}
