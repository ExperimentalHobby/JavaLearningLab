package com.javalab.jparestapi;

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

    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(ProductResponse::from).toList();
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

    private Product findProductOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
