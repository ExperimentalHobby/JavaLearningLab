package com.javalab.bookapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 書籍管理REST APIのコントローラー。
 * {@link BookService}をコンストラクタインジェクションで受け取る(DIの実践)。
 * 書籍が見つからない場合の404応答は{@link BookNotFoundException}の{@code @ResponseStatus}に委譲するため、
 * 各メソッドで個別に例外処理は行わない。
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * 登録済み書籍を全件取得する。
     * @return 書籍一覧(200)
     */
    @GetMapping
    public List<Book> findAll() {
        return bookService.findAll();
    }

    /**
     * 新規書籍を登録する。
     * @param request タイトル・著者を含むリクエストボディ
     * @return 登録された書籍(201)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody BookRequest request) {
        return bookService.create(request.title(), request.author());
    }

    /**
     * 指定IDの書籍を取得する。
     * @param id 書籍ID
     * @return 該当する書籍(200)。存在しない場合は{@link BookNotFoundException}により404
     */
    @GetMapping("/{id}")
    public Book findById(@PathVariable long id) {
        return bookService.findById(id);
    }

    /**
     * 指定IDの書籍を削除する。成功時は204。存在しない場合は{@link BookNotFoundException}により404。
     * @param id 書籍ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        bookService.delete(id);
    }
}
