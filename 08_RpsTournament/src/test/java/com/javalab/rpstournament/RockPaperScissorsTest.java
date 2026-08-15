package com.javalab.rpstournament;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link RockPaperScissors#playMatch} の1試合分の勝敗判定を検証するテスト。
 * あいこの場合に{@code null}を返す(このクラス単体では再戦しない)ことも確認する。
 */
class RockPaperScissorsTest {

    private final Player playerA = new Player("Alice");
    private final Player playerB = new Player("Bob");

    @Test
    void playerAWinsWhenPlayerAHandBeatsPlayerBHand() {
        Player winner = RockPaperScissors.playMatch(playerA, Hand.ROCK, playerB, Hand.SCISSORS);

        assertEquals(playerA, winner);
    }

    @Test
    void playerBWinsWhenPlayerBHandBeatsPlayerAHand() {
        Player winner = RockPaperScissors.playMatch(playerA, Hand.SCISSORS, playerB, Hand.ROCK);

        assertEquals(playerB, winner);
    }

    @Test
    void returnsNullWhenHandsAreEqual() {
        // 同じ手同士はあいこであり、勝者を決められないためnullを返す。
        // 再戦の判断は呼び出し側(Tournament.playRound)の責務であり、このクラスは関知しない。
        Player winner = RockPaperScissors.playMatch(playerA, Hand.ROCK, playerB, Hand.ROCK);

        assertNull(winner);
    }
}
