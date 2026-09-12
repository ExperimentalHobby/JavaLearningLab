package com.javalab.jparestapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PageResponse<ProductResponse> findAll(String name, Pageable pageable) {
        Page<Product> page = (name == null || name.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNameContainingIgnoreCase(name, pageable);
        return PageResponse.from(page.map(ProductResponse::from));
    }

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
     */
    public void fulfillOrder(List<OrderLine> lines) {
        for (OrderLine line : lines) {
            Product product = findProductOrThrow(line.productId());
            if (product.getStock() < line.quantity()) {
                throw new InsufficientStockException(line.productId());
            }
            product.setStock(product.getStock() - line.quantity());
        }
    }

    // idにnull許容性の注釈が無いため、Spring Dataの@NonNullApiとの不一致で誤検知される警告を抑制する。
    @SuppressWarnings("null")
    private Product findProductOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
