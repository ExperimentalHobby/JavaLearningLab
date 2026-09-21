package com.javalab.genericcollection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link CollectionUtils#max(List)} の境界型パラメータ({@code T extends Comparable<T>})を
 * 使ったジェネリックメソッドを検証するテスト。Integer/Stringという異なる型に対して
 * 同じmax()メソッドが型安全に動作することを確認する。
 */
class CollectionUtilsTest {

    @Test
    void maxReturnsLargestIntegerInList() {
        int result = CollectionUtils.max(List.of(3, 1, 4, 1, 5, 9, 2, 6));

        assertEquals(9, result);
    }

    @Test
    void maxReturnsLargestStringInList() {
        // Stringの自然順序(辞書順)で最大の"cherry"が選ばれることを確認する。
        String result = CollectionUtils.max(List.of("banana", "apple", "cherry"));

        assertEquals("cherry", result);
    }

    @Test
    void maxThrowsEmptyCollectionExceptionForEmptyList() {
        // 空リストのmaxがIllegalArgumentException、スタック/キューの空操作はEmptyCollectionExceptionと
        // 例外方針が不統一だったため、ライブラリ全体でEmptyCollectionExceptionに統一する。
        assertThrows(EmptyCollectionException.class, () -> CollectionUtils.max(List.<Integer>of()));
    }

    @Test
    void minReturnsSmallestIntegerInList() {
        int result = CollectionUtils.min(List.of(3, 1, 4, 1, 5, 9, 2, 6));

        assertEquals(1, result);
    }

    @Test
    void minThrowsEmptyCollectionExceptionForEmptyList() {
        assertThrows(EmptyCollectionException.class, () -> CollectionUtils.min(List.<Integer>of()));
    }

    @Test
    void filterReturnsOnlyElementsMatchingPredicate() {
        List<Integer> result = CollectionUtils.filter(List.of(1, 2, 3, 4, 5, 6), n -> n % 2 == 0);

        assertEquals(List.of(2, 4, 6), result);
    }

    @Test
    void mapTransformsEachElement() {
        List<String> result = CollectionUtils.map(List.of(1, 2, 3), n -> "No." + n);

        assertEquals(List.of("No.1", "No.2", "No.3"), result);
    }

    /**
     * {@code Comparable<T>}ではなく{@code Comparable<? super T>}(PECS)を境界型に使っていることを
     * 確認するテスト。{@code Base}自身は{@code Comparable}を実装せず、親クラス{@code Base}を
     * 実装する{@code Sub}のインスタンスでmax()が呼べることを型レベルで検証する
     * (旧境界型{@code T extends Comparable<T>}ではこの呼び出し自体がコンパイルエラーになる)。
     */
    @Test
    void maxAcceptsTypeWhereComparableIsImplementedBySuperclass() {
        List<Sub> items = List.of(new Sub(1), new Sub(3), new Sub(2));

        Sub result = CollectionUtils.max(items);

        assertEquals(3, result.value);
    }

    private static class Base implements Comparable<Base> {
        final int value;

        Base(int value) {
            this.value = value;
        }

        @Override
        public int compareTo(Base other) {
            return Integer.compare(value, other.value);
        }
    }

    private static class Sub extends Base {
        Sub(int value) {
            super(value);
        }
    }
}
