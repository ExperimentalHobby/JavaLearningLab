package com.javalab.jparestapi;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 複数商品の在庫を一括で引き当てる注文APIを提供する。 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final ProductService productService;

    public OrderController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public void fulfill(@RequestBody List<OrderLine> lines) {
        productService.fulfillOrder(lines);
    }
}
