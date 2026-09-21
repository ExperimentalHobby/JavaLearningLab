package com.javalab.jparestapi;

/**
 * 注文1明細分。指定商品を指定数量だけ引き当てる。
 * {@code @Valid}によるBean Validationのカスケード検証は、{@code List<OrderLine>}を直接
 * {@code @RequestBody}にした場合は要素までは検証されない(ラップするDTOのフィールドに
 * {@code List<@Valid OrderLine>}と書いた場合のみカスケードされる)ため、
 * 検証は{@link ProductService#fulfillOrder}側で明示的に行う。
 */
public record OrderLine(Long productId, int quantity) {
}
