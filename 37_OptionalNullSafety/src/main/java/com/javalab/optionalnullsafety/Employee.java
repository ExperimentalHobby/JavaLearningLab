package com.javalab.optionalnullsafety;

import java.util.Objects;

/**
 * 社員を表す不変な値オブジェクト。
 * 「値が存在しないかもしれない」ことは{@code Optional}で表現する方針のため、
 * フィールドには{@code Optional}を持たせない(アンチパターン回避)。
 * これとは別に、生成されたインスタンスのフィールドは常にnullであってはならないという
 * 不変条件を{@link Objects#requireNonNull}で保証する。
 * {@code Optional}が「呼び出し側に値が無いかもしれないことを伝える戻り値の型」であるのに対し、
 * {@code Objects.requireNonNull}は「そもそもnullを許さない」不変条件のチェックであり、役割が異なる。
 */
public record Employee(String id, String name, String email) {
    public Employee {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(email, "email must not be null");
    }
}
