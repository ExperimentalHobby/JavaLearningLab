package com.javalab.jparestapi;

import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPAの標準CRUDメソッド(save/findById/findAll/deleteById等)をそのまま利用する。 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
