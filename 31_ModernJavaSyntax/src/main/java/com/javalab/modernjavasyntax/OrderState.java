package com.javalab.modernjavasyntax;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;

/**
 * 注文の配送状態を表すsealed interface。
 * 取りうる状態を{@link Placed}/{@link Shipped}/{@link Delivered}/{@link Cancelled}の4種類に限定することで、
 * switch式での分岐漏れをコンパイル時に検出できるようにする。
 * {@code @JsonTypeInfo}/{@code @JsonSubTypes}で型情報をJSONに残すことで、
 * インターフェース型のフィールドとして持たせたままでもデシリアライズ時に元のrecord型へ復元できる
 * ({@link OrderStatePersistence}参照)。
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderState.Placed.class, name = "Placed"),
        @JsonSubTypes.Type(value = OrderState.Shipped.class, name = "Shipped"),
        @JsonSubTypes.Type(value = OrderState.Delivered.class, name = "Delivered"),
        @JsonSubTypes.Type(value = OrderState.Cancelled.class, name = "Cancelled"),
})
public sealed interface OrderState permits OrderState.Placed, OrderState.Shipped, OrderState.Delivered, OrderState.Cancelled {

    /** 状態の日本語ラベル。{@code list}表示やエラーメッセージの組み立てで共通利用する。 */
    default String label() {
        return switch (this) {
            case Placed _ -> "注文受付";
            case Shipped _ -> "発送済み";
            case Delivered _ -> "配達完了";
            case Cancelled _ -> "キャンセル";
        };
    }

    record Placed(LocalDate orderedDate) implements OrderState {
        public Placed {
            if (orderedDate == null) {
                throw new IllegalArgumentException("注文日は必須です");
            }
        }
    }

    record Shipped(LocalDate orderedDate, String trackingNumber, LocalDate shippedDate) implements OrderState {
        public Shipped {
            if (trackingNumber == null || trackingNumber.isBlank()) {
                throw new IllegalArgumentException("伝票番号は空にできません");
            }
        }
    }

    record Delivered(LocalDate orderedDate, String trackingNumber, LocalDate deliveredDate) implements OrderState {
        public Delivered {
            if (orderedDate == null) {
                throw new IllegalArgumentException("注文日は必須です");
            }
            if (trackingNumber == null || trackingNumber.isBlank()) {
                throw new IllegalArgumentException("伝票番号は空にできません");
            }
            if (deliveredDate == null) {
                throw new IllegalArgumentException("配達日は必須です");
            }
        }
    }

    record Cancelled(LocalDate orderedDate, String reason) implements OrderState {
        public Cancelled {
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("理由は空にできません");
            }
        }
    }
}
