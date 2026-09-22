package com.javalab.jpahibernate;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Main#run(Scanner, PrintStream, ProductRepository)} のREPLループを結合テストするクラス。
 */
class MainTest {

    private EntityManagerFactory entityManagerFactory;
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("productPU-test");
        repository = new ProductRepository(entityManagerFactory);
    }

    @AfterEach
    void tearDown() {
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

    @Test
    void runShowsErrorAndContinuesForUnknownCommand() {
        Scanner scanner = new Scanner("unknown foo\nadd ノート 150 100\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("不明なコマンドです"));
        // 不明なコマンドでループを抜けず、後続のコマンドが処理されることまで確認する
        assertTrue(result.contains("追加しました"));
    }

    @Test
    void runShowsNotFoundMessageForFindWithNonExistentId() {
        Scanner scanner = new Scanner("find 999\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("該当する商品が見つかりません: id=999"));
    }

    @Test
    void runShowsNotFoundMessageForUpdateWithNonExistentId() {
        Scanner scanner = new Scanner("update 999 200 80\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("該当する商品が見つかりません: id=999"));
        // 更新処理そのものが実行されていないことを確認する
        assertFalse(result.contains("更新しました"));
    }

    @Test
    void runShowsClearErrorForAddCommandWithMissingArguments() {
        // 修正前はparts[3]でArrayIndexOutOfBoundsExceptionとなり、
        // 「エラー: Index 3 out of bounds for length 3」という英語の内部例外メッセージが表示されていた。
        Scanner scanner = new Scanner("add ノート 150\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("使用方法: add <商品名> <価格> <在庫数>"));
    }

    @Test
    void runSearchCommandShowsOnlyMatchingProducts() {
        // search自体の出力(一覧形式"価格=...")で判定する。"追加しました"のechoに商品名が
        // 含まれてしまうため、それだけでは検索コマンドが実際に機能しているか確認できない。
        Scanner scanner = new Scanner("add ノート 150 100\nadd ペン 100 50\nsearch ノート\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("価格=150"));
        assertFalse(result.contains("価格=100"));
    }

    @Test
    void runShowsNotFoundMessageForDeleteWithNonExistentId() {
        Scanner scanner = new Scanner("delete 999\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("該当する商品が見つかりません: id=999"));
        assertFalse(result.contains("削除しました"));
    }

    @Test
    void runShowsClearErrorForFindCommandWithMissingArguments() {
        // 修正前はparts[1]でArrayIndexOutOfBoundsExceptionとなり、英語の内部例外メッセージが
        // 表示されていた。
        Scanner scanner = new Scanner("find\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("使用方法: find <id>"));
    }

    @Test
    void runShowsClearErrorForUpdateCommandWithMissingArguments() {
        Scanner scanner = new Scanner("add ノート 150 100\nupdate 1 200\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("使用方法: update <id> <価格> <在庫数>"));
    }

    @Test
    void runShowsClearErrorForDeleteCommandWithMissingArguments() {
        Scanner scanner = new Scanner("delete\nexit\n");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);

        Main.run(scanner, out, repository);

        String result = buffer.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("使用方法: delete <id>"));
    }
}
