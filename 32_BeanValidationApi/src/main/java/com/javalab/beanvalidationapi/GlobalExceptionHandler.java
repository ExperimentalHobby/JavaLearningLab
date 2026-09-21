package com.javalab.beanvalidationapi;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Bean Validationの検証エラー・リクエストの構文/型不正を一元的に400 Bad Requestへ変換するハンドラー。
 * 個々のコントローラーで例外処理を書く必要がないよう、アプリケーション全体に適用する。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** {@code @Valid}によるリクエストボディ(DTO)の検証エラー。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<FieldErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldErrorResponse(fe.getField(), fe.getDefaultMessage()))
                .toList();
    }

    /** {@code @Validated}によるメソッドパラメータ(パス変数・クエリパラメータ)の検証エラー。 */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<FieldErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(v -> new FieldErrorResponse(lastNode(v.getPropertyPath()), v.getMessage()))
                .toList();
    }

    /** 壊れたJSONボディなど、リクエストの読み取り自体に失敗した場合。 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ErrorResponse("リクエストボディの形式が不正です");
    }

    /** パス変数・クエリパラメータの型変換に失敗した場合(例: {@code /api/users/abc})。 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return new ErrorResponse("パラメータの形式が不正です: " + ex.getName());
    }

    // ConstraintViolationのプロパティパスは"findById.id"のようにメソッド名を含むため、
    // 末尾のノード(パラメータ名)だけを取り出してFieldErrorResponse.fieldに使う。
    private static String lastNode(Path path) {
        String name = "";
        for (Path.Node node : path) {
            name = node.getName();
        }
        return name;
    }
}
