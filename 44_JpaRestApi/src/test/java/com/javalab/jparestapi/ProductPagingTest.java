package com.javalab.jparestapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ProductService#findAll(String, org.springframework.data.domain.Pageable)}の
 * ページネーション・検索を検証する。{@code @Transactional}でテストごとにDB変更を自動ロールバックする。
 *
 * <p>他のテストクラス(結合テストなど)は{@code @Transactional}を使わず実コミットするため、
 * 同じ共有H2インスタンス上に他クラスが作成した商品データが残っている状態で本テストが実行され得る。
 * そのため、各テストは一意なマーカー文字列を商品名に含め、検索条件としてマーカーを指定することで
 * 「自分が作成したデータだけ」を対象に集計・検証する(他クラスのデータに影響されない設計)。</p>
 */
@SpringBootTest
@Transactional
class ProductPagingTest {

    private static final AtomicInteger MARKER_COUNTER = new AtomicInteger();

    @Autowired
    private ProductService productService;

    private static String uniqueMarker() {
        return "PAGINGTEST" + MARKER_COUNTER.incrementAndGet();
    }

    @Test
    void findAll_pageSize2_returnsTwoItemsAndCorrectTotalElements() {
        String marker = uniqueMarker();
        for (int i = 1; i <= 5; i++) {
            productService.create(new ProductRequest(marker + "-商品" + i, new BigDecimal("100"), 10));
        }

        PageResponse<ProductResponse> page = productService.findAll(marker, PageRequest.of(0, 2));

        assertEquals(2, page.content().size());
        assertEquals(5, page.totalElements());
        assertEquals(3, page.totalPages());
    }

    @Test
    void findAll_sortedByPriceDescending_returnsInDescendingOrder() {
        String marker = uniqueMarker();
        productService.create(new ProductRequest(marker + "-安い商品", new BigDecimal("100"), 10));
        productService.create(new ProductRequest(marker + "-高い商品", new BigDecimal("900"), 10));
        productService.create(new ProductRequest(marker + "-中間商品", new BigDecimal("500"), 10));

        PageResponse<ProductResponse> page = productService.findAll(
                marker, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "price")));

        assertEquals(marker + "-高い商品", page.content().get(0).name());
        assertEquals(marker + "-中間商品", page.content().get(1).name());
        assertEquals(marker + "-安い商品", page.content().get(2).name());
    }

    @Test
    void findAll_withNameFilter_returnsOnlyMatchingProducts() {
        String marker = uniqueMarker();
        productService.create(new ProductRequest(marker + "-ノートパソコン", new BigDecimal("80000"), 5));
        productService.create(new ProductRequest(marker + "-ノート", new BigDecimal("150"), 100));
        productService.create(new ProductRequest(marker + "-消しゴム", new BigDecimal("80"), 50));

        PageResponse<ProductResponse> page = productService.findAll(marker + "-ノート", PageRequest.of(0, 10));

        assertEquals(2, page.totalElements());
        assertTrue(page.content().stream().allMatch(p -> p.name().contains("ノート")));
    }

    @Test
    void findAll_secondPage_doesNotOverlapWithFirstPage() {
        String marker = uniqueMarker();
        for (int i = 1; i <= 5; i++) {
            productService.create(new ProductRequest(marker + "-商品" + i, new BigDecimal("100"), 10));
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        PageResponse<ProductResponse> firstPage = productService.findAll(marker, PageRequest.of(0, 2, sort));
        PageResponse<ProductResponse> secondPage = productService.findAll(marker, PageRequest.of(1, 2, sort));

        assertEquals(2, secondPage.content().size());
        assertTrue(firstPage.content().stream().noneMatch(
                p -> secondPage.content().stream().anyMatch(q -> q.id().equals(p.id()))));
    }
}
