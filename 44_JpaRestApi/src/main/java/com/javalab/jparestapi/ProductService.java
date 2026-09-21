package com.javalab.jparestapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品在庫管理のロジックを提供する。クラスレベルで{@code @Transactional}を付与し、
 * 各メソッドが1トランザクションとして実行されるようにしている。
 */
@Service
@Transactional
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductResponse create(ProductRequest request) {
        Product saved = repository.save(new Product(request.name(), request.price(), request.stock()));
        return ProductResponse.from(saved);
    }

    /** {@code name}が指定されていれば部分一致検索、なければ全件を対象にページング・ソートを適用する。 */
    // Spring Data(org.springframework.data.repositoryパッケージ)は@NonNullApiだが、
    // 呼び出し元のpageableにはnull許容性の注釈が無いため、誤検知される警告を抑制する。
    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAll(String name, Pageable pageable) {
        Page<Product> page = (name == null || name.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNameContainingIgnoreCase(name, pageable);
        return PageResponse.from(page.map(ProductResponse::from));
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductResponse.from(findProductOrThrow(id));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProductOrThrow(id);
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        return ProductResponse.from(product);
    }

    // findProductOrThrowの戻り値には注釈が無いため、Spring Dataの@NonNullApiとの不一致で
    // 誤検知される警告を抑制する。
    @SuppressWarnings("null")
    public void delete(Long id) {
        Product product = findProductOrThrow(id);
        repository.delete(product);
    }

    /**
     * 複数商品の在庫を一括で引き当てる。このメソッド全体が1トランザクションであるため、
     * 途中の明細で在庫不足が発生すると、それより前に引き当てた在庫の減算も含めて
     * 全てロールバックされる。
     * 各明細は{@link ProductRepository#findByIdForUpdate}で悲観ロックを取得してから読む。
     * 「在庫を読む→判定する→減算する」は本質的に競合状態になりうるため、
     * 同じ商品への同時引き当てはこのロックにより1件ずつ直列化される
     * (@Transactional単体はアトミック性は保証するが、この種の競合は防げない)。
     * @return 引き当て後の商品一覧
     */
    public List<ProductResponse> fulfillOrder(List<OrderLine> lines) {
        List<ProductResponse> results = new ArrayList<>();
        for (OrderLine line : lines) {
            validateOrderLine(line);
            Product product = repository.findByIdForUpdate(line.productId())
                    .orElseThrow(() -> new ProductNotFoundException(line.productId()));
            if (product.getStock() < line.quantity()) {
                throw new InsufficientStockException(line.productId());
            }
            product.setStock(product.getStock() - line.quantity());
            results.add(ProductResponse.from(product));
        }
        return results;
    }

    // productIdがnullだとfindByIdForUpdate(null)がInvalidDataAccessApiUsageException(500)になり、
    // quantityが負だと「在庫を判定してから減算する」ロジックを素通りして在庫が増えてしまっていたため、
    // 事前に検証する。
    private void validateOrderLine(OrderLine line) {
        if (line.productId() == null) {
            throw new IllegalArgumentException("商品IDは必須です");
        }
        if (line.quantity() <= 0) {
            throw new IllegalArgumentException("数量は正の数である必要があります: productId=" + line.productId());
        }
    }

    // idにnull許容性の注釈が無いため、Spring Dataの@NonNullApiとの不一致で誤検知される警告を抑制する。
    @SuppressWarnings("null")
    private Product findProductOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
