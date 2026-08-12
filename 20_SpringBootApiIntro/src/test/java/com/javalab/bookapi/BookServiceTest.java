package com.javalab.bookapi;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
