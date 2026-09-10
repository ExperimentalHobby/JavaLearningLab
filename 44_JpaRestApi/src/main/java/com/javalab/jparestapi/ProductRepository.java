package com.javalab.jparestapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPAの標準CRUDメソッド(save/findById/findAll/deleteById等)をそのまま利用する。 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** メソッド名からクエリを自動生成する(商品名の大文字小文字を区別しない部分一致検索)。 */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
