package com.javalab.jpahibernate;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * {@link Product}のCRUD操作を{@link EntityManager}経由で提供するリポジトリ。
 */
public class ProductRepository {

    private final EntityManager entityManager;

    public ProductRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * 商品を新規永続化する。
     * @param product 保存対象の商品(IDは未設定)
     * @return IDが採番された商品
     */
    public Product save(Product product) {
        entityManager.getTransaction().begin();
        entityManager.persist(product);
        entityManager.getTransaction().commit();
        return product;
    }

    /**
     * IDを指定して商品を取得する。
     * @param id 商品ID
     * @return 該当商品。存在しない場合は空
     */
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }

    /**
     * 全商品を取得する。
     * @return 商品一覧
     */
    public List<Product> findAll() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    /**
     * 既存商品の内容を更新する。
     * @param product 更新後の内容を持つ商品(既存IDを保持していること)
     * @return 更新後の商品
     */
    public Product update(Product product) {
        entityManager.getTransaction().begin();
        Product merged = entityManager.merge(product);
        entityManager.getTransaction().commit();
        return merged;
    }

    /**
     * IDを指定して商品を削除する。
     * @param id 商品ID
     * @return 削除できた場合はtrue、該当商品が存在しなかった場合はfalse
     */
    public boolean deleteById(Long id) {
        Product product = entityManager.find(Product.class, id);
        if (product == null) {
            return false;
        }
        entityManager.getTransaction().begin();
        entityManager.remove(product);
        entityManager.getTransaction().commit();
        return true;
    }
}
