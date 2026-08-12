package com.javalab.rpstournament;

/**
 * じゃんけん1試合の勝敗判定ロジック。
 */
public class RockPaperScissors {

    /**
     * 2人のプレイヤーの手から勝者を判定する。
     * @param playerA プレイヤーA
     * @param handA プレイヤーAの手
     * @param playerB プレイヤーB
     * @param handB プレイヤーBの手
     * @return 勝者、あいこの場合は{@code null}
     */
    public static Player playMatch(Player playerA, Hand handA, Player playerB, Hand handB) {
        if (handA.beats(handB)) {
            return playerA;
        }
        if (handB.beats(handA)) {
            return playerB;
        }
        return null;
    }
}
