package com.javalab.bookapi;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 書籍データをインメモリで管理するサービス層。
 * {@code @Service}によりSpringのDIコンテナへ登録され、{@link BookController}へ
 * コンストラクタインジェクションされる。
 */
@Service
public class BookService {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    /**
     * 新規書籍を登録する。
     * @param title タイトル
     * @param author 著者
     * @return 採番済みの{@link Book}
     */
    public Book create(String title, String author) {
        Book book = new Book(nextId.getAndIncrement(), title, author);
        books.put(book.id(), book);
        return book;
    }

    /**
     * 登録済み書籍を全件取得する。
     * @return 書籍一覧(ID昇順)
     */
    public List<Book> findAll() {
        // ConcurrentHashMap.values()の反復順序はハッシュ値依存で不定なため、
        // 呼び出し側から見て安定した順序になるようID昇順にソートする。
        return books.values().stream()
                .sorted(Comparator.comparingLong(Book::id))
                .toList();
    }

    /**
     * 指定IDの書籍を取得する。
     * @param id 書籍ID
     * @return 該当する{@link Book}
     * @throws BookNotFoundException 該当する書籍が存在しない場合
     */
    public Book findById(long id) {
        Book book = books.get(id);
        if (book == null) {
            throw new BookNotFoundException(id);
        }
        return book;
    }

    /**
     * 指定IDの書籍を更新する。
     * @param id 書籍ID
     * @param title 新しいタイトル
     * @param author 新しい著者
     * @return 更新後の{@link Book}
     * @throws BookNotFoundException 該当する書籍が存在しない場合
     */
    public Book update(long id, String title, String author) {
        findById(id);
        Book updated = new Book(id, title, author);
        books.put(id, updated);
        return updated;
    }

    /**
     * 指定IDの書籍を削除する。
     * @param id 書籍ID
     * @throws BookNotFoundException 該当する書籍が存在しない場合
     */
    public void delete(long id) {
        if (books.remove(id) == null) {
            throw new BookNotFoundException(id);
        }
    }
}
