package com.javalab.junitpractice;

/**
 * {@link OrderNotificationService#notifyOrderConfirmed(Order)} の結果。
 * 送信をスキップした場合に理由を呼び出し側(CLI等)が判別できるようにする
 * (修正前はスキップ時に何も通知されず、利用者には「何も起きなかった」ようにしか見えなかった)。
 */
public enum NotificationOutcome {

    /** メールを送信した。 */
    SENT,

    /** 合計金額が0円以下のため送信をスキップした。 */
    SKIPPED_NON_POSITIVE_TOTAL,

    /** メールアドレスの形式が不正なため送信をスキップした。 */
    SKIPPED_INVALID_EMAIL
}
