package com.javalab.bookapi;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link BookService} のインメモリCRUDロジックを検証するテスト。
 * SpringのDIコンテナを起動せず{@code new BookService()}で直接インスタンス化しており、
 * HTTP層を経由しないビジネスロジック単体の高速なテストになっている
 * (HTTP層の検証は{@link BookControllerTest}が担当)。
 */
class BookServiceTest {

    private final BookService service = new BookService();

    @Test
    void createAssignsIdAndFindAllReturnsCreatedBook() {
        Book created = service.create("吾輩は猫である", "夏目漱石");

        List<Book> books = service.findAll();

        assertEquals(1, books.size());
        assertEquals(created.id(), books.get(0).id());
        assertEquals("吾輩は猫である", books.get(0).title());
        assertEquals("夏目漱石", books.get(0).author());
    }

    @Test
    void findAllReturnsBooksOrderedById() {
        // ConcurrentHashMap.values()由来のList.copyOfを返しており、一覧の順序が不定だった問題への対応。
        // 複数件登録した後、常にID昇順で返ることを確認する。
        service.create("三四郎", "夏目漱石");
        service.create("坊っちゃん", "夏目漱石");
        service.create("こころ", "夏目漱石");

        List<Long> ids = service.findAll().stream().map(book -> book.id()).toList();

        assertEquals(ids.stream().sorted().toList(), ids);
    }

    @Test
    void findByIdThrowsBookNotFoundExceptionForNonExistentId() {
        assertThrows(BookNotFoundException.class, () -> service.findById(999));
    }

    @Test
    void deleteRemovesBookAndThrowsBookNotFoundExceptionForNonExistentId() {
        Book created = service.create("吾輩は猫である", "夏目漱石");

        service.delete(created.id());

        assertThrows(BookNotFoundException.class, () -> service.findById(created.id()));
        assertThrows(BookNotFoundException.class, () -> service.delete(999));
    }
}
