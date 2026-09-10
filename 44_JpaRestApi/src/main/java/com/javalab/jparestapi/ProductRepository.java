package com.javalab.jparestapi;

import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPAの標準CRUDメソッド(save/findById/findAll/deleteById等)をそのまま利用する。 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** メソッド名からクエリを自動生成する(在庫が閾値未満の商品数)。 */
    long countByStockLessThan(int threshold);
}
