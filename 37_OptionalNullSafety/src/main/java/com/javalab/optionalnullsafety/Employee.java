package com.javalab.optionalnullsafety;

/**
 * 社員を表す不変な値オブジェクト。
 * 「値が存在しないかもしれない」ことは{@code Optional}で表現する方針のため、
 * フィールドには{@code Optional}を持たせない(アンチパターン回避)。
 */
public record Employee(String id, String name, String email) {
}
