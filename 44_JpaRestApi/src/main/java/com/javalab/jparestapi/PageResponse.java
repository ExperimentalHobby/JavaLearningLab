package com.javalab.jparestapi;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * ページング結果のレスポンスDTO。Spring Data JPAの{@link Page}をそのままJSONシリアライズせず、
 * 独自DTOへ変換することで実装詳細(Spring Data JPAへの依存)をAPIの外に晒さない。
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
