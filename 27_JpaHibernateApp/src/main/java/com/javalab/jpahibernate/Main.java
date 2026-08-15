package com.javalab.jpahibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 簡易ORM/DB操作アプリのエントリーポイント。
 * 標準入力からコマンドを読み取り、H2上の商品データをJPA/Hibernate経由でCRUD操作する対話型REPLを提供する。
 */
public class Main {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("productPU");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            ProductRepository repository = new ProductRepository(entityManager);
            run(new Scanner(System.in), System.out, repository);
        } finally {
            entityManager.close();
            entityManagerFactory.close();
        }
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}/{@link ProductRepository}を
     * 差し替えられるよう分離している。
     * コマンド: {@code add <商品名> <価格> <在庫数>} / {@code list} / {@code find <id>} /
     * {@code update <id> <価格> <在庫数>} / {@code delete <id>} / {@code exit}。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     * @param repository 商品の永続化を担うリポジトリ
     */
    static void run(Scanner scanner, PrintStream out, ProductRepository repository) {
        out.println("簡易ORM/DB操作アプリ。コマンド: "
                + "add <商品名> <価格> <在庫数> / list / find <id> / update <id> <価格> <在庫数> / delete <id> / exit");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String command = parts[0];
            try {
                switch (command) {
                    case "exit" -> {
                        return;
                    }
                    case "add" -> handleAdd(repository, parts, out);
                    case "list" -> handleList(repository, out);
                    case "find" -> handleFind(repository, parts, out);
                    case "update" -> handleUpdate(repository, parts, out);
                    case "delete" -> handleDelete(repository, parts, out);
                    default -> out.println("不明なコマンドです: " + line);
                }
            } catch (RuntimeException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleAdd(ProductRepository repository, String[] parts, PrintStream out) {
        Product saved = repository.save(new Product(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
        out.println("追加しました: id=" + saved.getId() + " " + saved.getName());
    }

    private static void handleList(ProductRepository repository, PrintStream out) {
        List<Product> products = repository.findAll();
        if (products.isEmpty()) {
            out.println("商品はありません");
            return;
        }
        for (Product product : products) {
            out.println(product.getId() + ": " + product.getName()
                    + " 価格=" + product.getPrice() + " 在庫=" + product.getStock());
        }
    }

    private static void handleFind(ProductRepository repository, String[] parts, PrintStream out) {
        Optional<Product> found = repository.findById(Long.parseLong(parts[1]));
        if (found.isEmpty()) {
            out.println("該当する商品が見つかりません: id=" + parts[1]);
            return;
        }
        Product product = found.get();
        out.println(product.getId() + ": " + product.getName()
                + " 価格=" + product.getPrice() + " 在庫=" + product.getStock());
    }

    private static void handleUpdate(ProductRepository repository, String[] parts, PrintStream out) {
        long id = Long.parseLong(parts[1]);
        Optional<Product> found = repository.findById(id);
        if (found.isEmpty()) {
            out.println("該当する商品が見つかりません: id=" + id);
            return;
        }
        Product product = found.get();
        product.setPrice(Integer.parseInt(parts[2]));
        product.setStock(Integer.parseInt(parts[3]));
        repository.update(product);
        out.println("更新しました: id=" + id);
    }

    private static void handleDelete(ProductRepository repository, String[] parts, PrintStream out) {
        long id = Long.parseLong(parts[1]);
        boolean deleted = repository.deleteById(id);
        out.println(deleted ? "削除しました: id=" + id : "該当する商品が見つかりません: id=" + id);
    }
}
