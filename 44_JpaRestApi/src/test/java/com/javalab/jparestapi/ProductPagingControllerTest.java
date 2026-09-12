package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@code GET /api/products}のページネーション・検索をHTTP経由で結合テストするクラス。
 * 他クラスと共有DBのため、テストごとに一意なマーカーで自分が作成したデータのみを対象にする
 * ({@link ProductPagingTest}と同じ方針)。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductPagingControllerTest {

    private static final AtomicInteger MARKER_COUNTER = new AtomicInteger();

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductService productService;

    private static String uniqueMarker() {
        return "PAGINGCTRL" + MARKER_COUNTER.incrementAndGet();
    }

    @Test
    void getProducts_withNameAndPagingParams_returnsFilteredPage() {
        String marker = uniqueMarker();
        productService.create(new ProductRequest(marker + "-ノートパソコン", new BigDecimal("80000"), 5));
        productService.create(new ProductRequest(marker + "-ノート", new BigDecimal("150"), 100));
        productService.create(new ProductRequest(marker + "-消しゴム", new BigDecimal("80"), 50));

        // URI変数として渡すことで、日本語を含むnameの符号化をRestTemplateに任せる
        // (事前にURLエンコード済み文字列を組み立てて渡すと、内部でさらに符号化され二重符号化になり
        // サーバー側で意図した文字列に復元できなくなる)。
        ResponseEntity<PageResponse<ProductResponse>> response = restTemplate.exchange(
                "/api/products?name={name}&page={page}&size={size}",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<PageResponse<ProductResponse>>() {
                },
                marker + "-ノート", 0, 10);
        PageResponse<ProductResponse> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals(2, body.totalElements());
    }
}
