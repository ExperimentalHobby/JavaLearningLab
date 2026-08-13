package com.javalab.bookapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookService bookService;

    @Test
    void getBooksReturnsRegisteredBooks() {
        bookService.create("坊っちゃん", "夏目漱石");

        ResponseEntity<Book[]> response = restTemplate.getForEntity("/api/books", Book[].class);
        Book[] body = response.getBody();

        assertNotNull(body);
        List<Book> books = List.of(body);
        assertTrue(books.stream().anyMatch(b -> b.title().equals("坊っちゃん")));
    }

    @Test
    void postBooksCreatesBookAndReturns201() {
        BookRequest request = new BookRequest("こころ", "夏目漱石");

        ResponseEntity<Book> response = restTemplate.postForEntity("/api/books", request, Book.class);
        Book created = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(created);
        assertEquals("こころ", created.title());
        assertEquals("夏目漱石", created.author());
    }

    @Test
    void getBookByIdReturnsBookForExistentId() {
        Book created = bookService.create("三四郎", "夏目漱石");

        ResponseEntity<Book> response = restTemplate.getForEntity("/api/books/" + created.id(), Book.class);
        Book body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("三四郎", body.title());
    }

    @Test
    void getBookByIdReturns404ForNonExistentId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/books/999999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteBookRemovesBookAndReturns204() {
        Book created = bookService.create("それから", "夏目漱石");

        ResponseEntity<Void> response =
                restTemplate.exchange("/api/books/" + created.id(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertThrows(BookNotFoundException.class, () -> bookService.findById(created.id()));
    }
}
