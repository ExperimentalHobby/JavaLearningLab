package com.javalab.jpahibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@link Product}のCRUD操作を{@link EntityManagerFactory}経由で提供するリポジトリ。
 * {@link EntityManager}をアプリ起動から終了まで使い回すとJPAの1次キャッシュが際限なく肥大化し、
 * find/mergeの挙動(managedなのかdetachedなのか)も追いにくくなるため、操作ごとに
 * {@link EntityManagerFactory#createEntityManager()}で生成し使用後は必ずcloseする設計にしている。
 */
public class ProductRepository {

    private final EntityManagerFactory entityManagerFactory;

    public ProductRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * 商品を新規永続化する。
     * @param product 保存対象の商品(IDは未設定)
     * @return IDが採番された商品
     */
    public Product save(Product product) {
        return inTransaction(em -> {
            em.persist(product);
            return product;
        });
    }

    /**
     * IDを指定して商品を取得する。
     * @param id 商品ID
     * @return 該当商品。存在しない場合は空
     */
    public Optional<Product> findById(Long id) {
        return withEntityManager(em -> Optional.ofNullable(em.find(Product.class, id)));
    }

    /**
     * 全商品を取得する。
     * @return 商品一覧
     */
    public List<Product> findAll() {
        return withEntityManager(em -> em.createQuery("SELECT p FROM Product p", Product.class).getResultList());
    }

    /**
     * 商品をID順に指定件数分取得する(ページング)。
     * @param offset 取得を開始する位置(0始まり)
     * @param limit 取得する最大件数
     * @return 該当ページの商品一覧
     */
    public List<Product> findAll(int offset, int limit) {
        return withEntityManager(em -> em.createQuery("SELECT p FROM Product p ORDER BY p.id", Product.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList());
    }

    /**
     * 商品名が完全一致する商品を検索する。{@link Product}に定義した{@code @NamedQuery}
     * (JPQLのパラメータバインド)を使用する。
     * @param name 検索対象の商品名
     * @return 該当する商品一覧
     */
    public List<Product> findByName(String name) {
        return withEntityManager(em -> em.createNamedQuery("Product.findByName", Product.class)
                .setParameter("name", name)
                .getResultList());
    }

    /**
     * 既存商品の内容を更新する。
     * @param product 更新後の内容を持つ商品(既存IDを保持していること)
     * @return 更新後の商品
     */
    public Product update(Product product) {
        return inTransaction(em -> em.merge(product));
    }

    /**
     * IDを指定して商品を削除する。
     * @param id 商品ID
     * @return 削除できた場合はtrue、該当商品が存在しなかった場合はfalse
     */
    public boolean deleteById(Long id) {
        return inTransaction(em -> {
            Product product = em.find(Product.class, id);
            if (product == null) {
                return false;
            }
            em.remove(product);
            return true;
        });
    }

    /**
     * 操作用の{@link EntityManager}を生成し、トランザクション内で処理を実行する。
     * 処理が例外を投げた場合はrollback()してから例外を再スローする(修正前はrollback()を
     * 呼んでおらず、commit()失敗時にEntityTransactionがactiveのまま残り、以降の全操作が
     * 「Transaction already active」で失敗するようになっていた)。
     */
    private <T> T inTransaction(Function<EntityManager, T> operation) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            T result = operation.apply(em);
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * 操作用の{@link EntityManager}を生成し、読み取り専用の処理を実行してからcloseする。
     */
    private <T> T withEntityManager(Function<EntityManager, T> operation) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            return operation.apply(em);
        } finally {
            em.close();
        }
    }
}
