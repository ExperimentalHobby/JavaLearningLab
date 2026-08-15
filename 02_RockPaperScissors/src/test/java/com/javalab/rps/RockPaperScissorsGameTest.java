package com.javalab.rps;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link RockPaperScissorsGame} の勝敗判定(judge)とコンピュータの手の抽選(randomHand)を検証するテスト。
 */
class RockPaperScissorsGameTest {

    @Test
    void playerWinsWhenPlayerHandBeatsComputerHand() {
        assertEquals(Result.PLAYER_WIN, RockPaperScissorsGame.judge(Hand.ROCK, Hand.SCISSORS));
    }

    @Test
    void computerWinsWhenComputerHandBeatsPlayerHand() {
        assertEquals(Result.COMPUTER_WIN, RockPaperScissorsGame.judge(Hand.SCISSORS, Hand.ROCK));
    }

    @Test
    void drawWhenBothHandsAreSame() {
        assertEquals(Result.DRAW, RockPaperScissorsGame.judge(Hand.ROCK, Hand.ROCK));
    }

    @Test
    void randomHandMapsNextIntZeroToRock() {
        // randomHand()はHand.values()[random.nextInt(...)]で選ぶ実装のため、
        // nextInt()の戻り値を固定すれば、対応する手が決定的に選ばれることを検証できる。
        // Hand列挙順(ROCK, SCISSORS, PAPER)に依存したテストであることに注意。
        Random fixed = fixedRandom(0);

        assertEquals(Hand.ROCK, RockPaperScissorsGame.randomHand(fixed));
    }

    @Test
    void randomHandMapsNextIntOneToScissors() {
        Random fixed = fixedRandom(1);

        assertEquals(Hand.SCISSORS, RockPaperScissorsGame.randomHand(fixed));
    }

    @Test
    void randomHandMapsNextIntTwoToPaper() {
        Random fixed = fixedRandom(2);

        assertEquals(Hand.PAPER, RockPaperScissorsGame.randomHand(fixed));
    }

    /**
     * {@link Random#nextInt(int)} が常に指定値を返すテスト用のスタブを作る。
     * モックライブラリを使わず、匿名クラスによる最小限の差し替えで乱数を決定的にしている。
     * @param value nextInt()が常に返す値
     * @return 固定値を返すRandom
     */
    private static Random fixedRandom(int value) {
        return new Random() {
            @Override
            public int nextInt(int bound) {
                return value;
            }
        };
    }
}
