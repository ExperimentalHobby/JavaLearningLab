package com.javalab.budget;

/**
 * 家計簿の1件が収入か支出かを表す種別。
 */
public enum EntryType {
    INCOME,
    EXPENSE;

    /**
     * 画面表示用の日本語名を返す。{@code JComboBox<EntryType>}の既定表示は列挙子名(英語)の
     * ままになるため、専用のレンダラーからこのメソッドを使って表示する。
     * @return "収入" または "支出"
     */
    public String getDisplayName() {
        return switch (this) {
            case INCOME -> "収入";
            case EXPENSE -> "支出";
        };
    }
}
