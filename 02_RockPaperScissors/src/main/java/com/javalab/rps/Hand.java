package com.javalab.rps;

/**
 * じゃんけんの手。列挙順(ROCK → SCISSORS → PAPER)は
 * {@link RockPaperScissorsGame#randomHand(java.util.Random)} の乱数マッピングにも利用される。
 */
public enum Hand {
    ROCK,
    SCISSORS,
    PAPER;

    /**
     * この手が相手の手に勝つかを判定する(グー→チョキ→パー→グー の三すくみ)。
     * @param other 相手の手
     * @return このHandがotherに勝つ場合true
     */
    public boolean beats(Hand other) {
        return (this == ROCK && other == SCISSORS)
                || (this == SCISSORS && other == PAPER)
                || (this == PAPER && other == ROCK);
    }
}
