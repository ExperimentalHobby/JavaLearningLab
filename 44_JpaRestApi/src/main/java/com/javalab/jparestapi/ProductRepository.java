package com.javalab.jparestapi;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/** Spring Data JPAの標準CRUDメソッド(save/findById/findAll/deleteById等)をそのまま利用する。 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** メソッド名からクエリを自動生成する(在庫が閾値未満の商品数)。 */
    long countByStockLessThan(int threshold);

    /** メソッド名からクエリを自動生成する(商品名の大文字小文字を区別しない部分一致検索)。 */
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * 行に悲観ロック({@code SELECT ... FOR UPDATE}相当)を取得したうえで取得する。
     * 在庫の引き当て(読む→判定する→減算する)のように、読んでから書くまでの間に
     * 他のトランザクションの割り込みを許してはならない場面で使う。ロックは呼び出し元の
     * トランザクションが終わるまで保持され、他のトランザクションは解放を待たされる。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
