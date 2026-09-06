package com.javalab.jparestapi;

/** 注文1明細分。指定商品を指定数量だけ引き当てる。 */
public record OrderLine(Long productId, int quantity) {
}
