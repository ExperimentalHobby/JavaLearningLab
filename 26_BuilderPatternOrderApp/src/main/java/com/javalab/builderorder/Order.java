package com.javalab.builderorder;

import java.util.ArrayList;
import java.util.List;

/**
 * 注文を表す不変オブジェクト。必須項目とオプション項目が混在するため、
 * {@link Builder}によるフルーエントAPIで段階的に組み立てる(Builderパターン)。
 */
public class Order {

    private final String customerName;
    private final String shippingAddress;
    private final List<OrderItem> items;
    private final String paymentMethod;
    private final boolean giftWrapping;
    private final String note;

    private Order(Builder builder) {
        this.customerName = builder.customerName;
        this.shippingAddress = builder.shippingAddress;
        this.items = List.copyOf(builder.items);
        this.paymentMethod = builder.paymentMethod;
        this.giftWrapping = builder.giftWrapping;
        this.note = builder.note;
    }

    /**
     * @return 顧客名
     */
    public String customerName() {
        return customerName;
    }

    /**
     * @return 配送先住所
     */
    public String shippingAddress() {
        return shippingAddress;
    }

    /**
     * @return 注文明細一覧(不変)
     */
    public List<OrderItem> items() {
        return items;
    }

    /**
     * 全明細の小計を合計した注文合計金額を返す。
     * @return 合計金額
     */
    public int totalAmount() {
        return items.stream().mapToInt(OrderItem::subtotal).sum();
    }

    /**
     * @return 支払方法
     */
    public String paymentMethod() {
        return paymentMethod;
    }

    /**
     * @return ギフトラッピングの有無
     */
    public boolean giftWrapping() {
        return giftWrapping;
    }

    /**
     * @return 注文メモ
     */
    public String note() {
        return note;
    }

    /**
     * 注文内容を人が読める形式に整形したサマリー文字列を返す。
     * @return 複数行の注文サマリー
     */
    public String toSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("注文確定: ").append(customerName).append(" 様\n");
        sb.append("配送先: ").append(shippingAddress).append('\n');
        sb.append("商品:\n");
        for (OrderItem item : items) {
            sb.append("  - ").append(item.productName())
                    .append(" x").append(item.quantity())
                    .append(" @").append(item.unitPrice()).append("円")
                    .append(" = ").append(item.subtotal()).append("円\n");
        }
        sb.append("合計金額: ").append(totalAmount()).append("円\n");
        sb.append("支払方法: ").append(paymentMethod).append('\n');
        sb.append("ギフトラッピング: ").append(giftWrapping ? "あり" : "なし").append('\n');
        sb.append("メモ: ").append(note.isEmpty() ? "(なし)" : note);
        return sb.toString();
    }

    /**
     * {@link Order}を段階的に組み立てるBuilder。
     * 顧客名・配送先住所は必須のためコンストラクタで受け取り、それ以外はフルーエントAPIで追加する。
     */
    public static class Builder {

        private final String customerName;
        private final String shippingAddress;
        private final List<OrderItem> items = new ArrayList<>();
        private String paymentMethod = "代金引換";
        private boolean giftWrapping = false;
        private String note = "";

        /**
         * 必須項目を指定してBuilderを開始する。
         * @param customerName 顧客名
         * @param shippingAddress 配送先住所
         * @throws IllegalArgumentException 顧客名または配送先住所が空の場合
         */
        public Builder(String customerName, String shippingAddress) {
            if (customerName == null || customerName.isBlank()) {
                throw new IllegalArgumentException("顧客名は必須です");
            }
            if (shippingAddress == null || shippingAddress.isBlank()) {
                throw new IllegalArgumentException("配送先住所は必須です");
            }
            this.customerName = customerName;
            this.shippingAddress = shippingAddress;
        }

        /**
         * 注文明細を1件追加する。
         * @param productName 商品名
         * @param quantity 数量
         * @param unitPrice 単価
         * @return このBuilder自身(メソッドチェーン用)
         */
        public Builder addItem(String productName, int quantity, int unitPrice) {
            items.add(new OrderItem(productName, quantity, unitPrice));
            return this;
        }

        /**
         * 支払方法を設定する。未指定の場合は「代金引換」が既定値となる。
         * @param paymentMethod 支払方法
         * @return このBuilder自身(メソッドチェーン用)
         */
        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        /**
         * ギフトラッピングの有無を設定する。未指定の場合はfalseが既定値となる。
         * @param giftWrapping ギフトラッピングする場合はtrue
         * @return このBuilder自身(メソッドチェーン用)
         */
        public Builder giftWrap(boolean giftWrapping) {
            this.giftWrapping = giftWrapping;
            return this;
        }

        /**
         * 注文メモを設定する。未指定の場合は空文字が既定値となる。
         * @param note メモ
         * @return このBuilder自身(メソッドチェーン用)
         */
        public Builder note(String note) {
            this.note = note;
            return this;
        }

        /**
         * これまでに設定した内容から{@link Order}を生成する。
         * @return 構築済みの注文
         * @throws IllegalStateException 商品が1件も追加されていない場合
         */
        public Order build() {
            if (items.isEmpty()) {
                throw new IllegalStateException("商品が1件も追加されていません");
            }
            return new Order(this);
        }
    }
}
