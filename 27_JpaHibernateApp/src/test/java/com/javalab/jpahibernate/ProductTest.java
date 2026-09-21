package com.javalab.jpahibernate;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * {@link Product} の{@code equals}/{@code hashCode}実装を検証するテスト。
 * JPAエンティティのequals/hashCodeは、Hibernateのプロキシ化やid未採番の永続化前インスタンスとの
 * 比較で問題を起こしやすいため定石(id基準のequals、hashCodeはクラス固定)に沿って実装している。
 */
class ProductTest {

    @Test
    void equalsReturnsTrueForSameId() throws Exception {
        Product a = new Product("ノート", 150, 100);
        Product b = new Product("ペン", 100, 50);
        setId(a, 1L);
        setId(b, 1L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsReturnsFalseForDifferentId() throws Exception {
        Product a = new Product("ノート", 150, 100);
        Product b = new Product("ノート", 150, 100);
        setId(a, 1L);
        setId(b, 2L);

        assertNotEquals(a, b);
    }

    @Test
    void equalsReturnsFalseWhenIdsAreBothNull() {
        // idが採番される前(永続化前)のインスタンス同士は、同一インスタンスでない限り
        // 等しいとみなさない(全ての未永続化インスタンスが「等しい」扱いになるのを避けるため)。
        Product a = new Product("ノート", 150, 100);
        Product b = new Product("ノート", 150, 100);

        assertNotEquals(a, b);
    }

    @Test
    void hashCodeStaysConstantBeforeAndAfterIdIsAssigned() throws Exception {
        // hashCodeはidに依存させると、id採番前後でハッシュ値が変化してしまい、
        // HashSet等に入れた後にidが採番されるケースで要素を見失う恐れがある。
        // getClass().hashCode()のようにid採番前後で不変な値にする。
        Product product = new Product("ノート", 150, 100);
        int hashBefore = product.hashCode();

        setId(product, 1L);

        assertEquals(hashBefore, product.hashCode());
    }

    private static void setId(Product product, Long id) throws Exception {
        Field field = Product.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(product, id);
    }
}
