package com.javalab.jpahibernate;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ProductRepository} のJPA/HibernateによるCRUD操作を検証するテスト。
 * モックは使わず、実際に起動したH2インメモリDBに対してEntityManager経由でSQLを発行する。
 * テストごとに{@code @BeforeEach}で新しい{@link EntityManagerFactory}を生成することで、
 * DBの状態がテスト間で持ち越されないようにしている。
 */
class ProductRepositoryTest {

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
    void saveAssignsGeneratedIdToNewProduct() {
        Product product = new Product("ノート", 150, 100);

        Product saved = repository.save(product);

        assertNotNull(saved.getId());
    }

    @Test
    void findByIdReturnsSavedProduct() {
        Product saved = repository.save(new Product("ノート", 150, 100));

        Optional<Product> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("ノート", found.get().getName());
        assertEquals(150, found.get().getPrice());
        assertEquals(100, found.get().getStock());
    }

    @Test
    void findByIdReturnsEmptyWhenProductDoesNotExist() {
        Optional<Product> found = repository.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findAllReturnsAllSavedProducts() {
        repository.save(new Product("ノート", 150, 100));
        repository.save(new Product("ペン", 100, 50));

        List<Product> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void updateReflectsChangesToExistingProduct() {
        Product saved = repository.save(new Product("ノート", 150, 100));
        saved.setPrice(200);
        saved.setStock(80);

        repository.update(saved);
        Optional<Product> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(200, found.get().getPrice());
        assertEquals(80, found.get().getStock());
    }

    @Test
    void deleteByIdRemovesProductSoThatFindByIdReturnsEmpty() {
        Product saved = repository.save(new Product("ノート", 150, 100));

        boolean deleted = repository.deleteById(saved.getId());

        assertTrue(deleted);
        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void saveRollsBackAndAllowsSubsequentOperationsWhenCommitFails() {
        // nameは@Column(nullable = false)のため、name=nullの商品はDB制約違反でcommit()時に失敗する。
        // 修正前はrollback()を呼んでいなかったため、EntityTransactionがactiveのまま残り、
        // 以降の全操作が「Transaction already active」で失敗するようになっていた。
        Product invalid = new Product(null, 100, 10);

        assertThrows(PersistenceException.class, () -> repository.save(invalid));

        // ロールバック後も正常に操作を継続できることを確認する(修正前はここで例外になっていた)。
        Product valid = repository.save(new Product("ノート", 150, 100));
        assertNotNull(valid.getId());
    }

    @Test
    void findByNameReturnsProductsMatchingExactName() {
        // @NamedQueryによるJPQLのパラメータバインドを扱う題材として、名前検索を追加した。
        repository.save(new Product("ノート", 150, 100));
        repository.save(new Product("ペン", 100, 50));
        repository.save(new Product("ノート", 180, 30));

        List<Product> found = repository.findByName("ノート");

        assertEquals(2, found.size());
    }

    @Test
    void findByNameReturnsEmptyListWhenNoProductMatches() {
        repository.save(new Product("ノート", 150, 100));

        List<Product> found = repository.findByName("存在しない商品");

        assertTrue(found.isEmpty());
    }

    @Test
    void findAllWithPagingReturnsOnlyRequestedPage() {
        // ページングを扱う題材として、findAllにoffset/limitを指定できるオーバーロードを追加した。
        for (int i = 1; i <= 5; i++) {
            repository.save(new Product("商品" + i, 100 * i, 10));
        }

        List<Product> page = repository.findAll(2, 2);

        assertEquals(2, page.size());
        assertEquals("商品3", page.get(0).getName());
        assertEquals("商品4", page.get(1).getName());
    }

    @Test
    void findByIdReturnsDetachedEntityNotAutomaticallyPersisted() {
        // EntityManagerをアプリ起動から終了まで使い回す設計だと、find()で取得したエンティティが
        // managedのままになり、update()を呼ばなくても後続の操作で1次キャッシュ経由で
        // 変更が「反映されたように見えてしまう」(実際にはDBへcommitされていない)という
        // 追いにくい挙動になっていた。操作ごとにEntityManagerを閉じることで、find()の戻り値は
        // 即座にdetachedとなり、update()を呼ばない限り変更が反映されないことを確認する。
        Product saved = repository.save(new Product("ノート", 150, 100));

        Product found = repository.findById(saved.getId()).orElseThrow();
        found.setPrice(999);
        // 意図的にupdate()を呼ばない。

        Product reloaded = repository.findById(saved.getId()).orElseThrow();
        assertEquals(150, reloaded.getPrice());
    }
}
