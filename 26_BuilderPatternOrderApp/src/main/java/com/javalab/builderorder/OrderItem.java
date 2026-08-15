package com.javalab.builderorder;

/**
 * 注文明細の1行を表す不変レコード。
 * @param productName 商品名
 * @param quantity 数量
 * @param unitPrice 単価
 */
public record OrderItem(String productName, int quantity, int unitPrice) {

    /**
     * 小計(数量×単価)を返す。
     * @return 小計
     */
    public int subtotal() {
        return quantity * unitPrice;
    }
}
