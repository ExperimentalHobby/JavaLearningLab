package com.javalab.jpahibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * 商品を表すJPAエンティティ。
 * アノテーションベースのDBマッピングを学ぶため、フィールドに直接JPAアノテーションを付与している。
 */
@Entity
@Table(name = "products")
@NamedQuery(
        name = "Product.findByName",
        query = "SELECT p FROM Product p WHERE p.name = :name")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int price;

    private int stock;

    /** JPA仕様が要求するデフォルトコンストラクタ。 */
    protected Product() {
    }

    /**
     * @param name 商品名
     * @param price 価格
     * @param stock 在庫数
     */
    public Product(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * id基準の同一性判定。JPAエンティティのequalsは、Hibernateのプロキシ化やid未採番の
     * 永続化前インスタンスとの比較で問題を起こしやすいため、idがnullの場合は
     * (同一インスタンスでない限り)等しいとみなさない定石に沿っている。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    /**
     * hashCodeをidに依存させると、id採番前後でハッシュ値が変化しHashSet等で要素を
     * 見失う恐れがあるため、id採番前後で不変なクラス固定値を返す。
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
