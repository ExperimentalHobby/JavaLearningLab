package com.javalab.rps;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Hand#beats(Hand)} の三すくみ判定ロジックを検証するテスト。
 * 「勝つ組み合わせ」「勝てない組み合わせ(同じ手・負ける手)」の両方を網羅する。
 */
class HandTest {

    @Test
    void rockBeatsScissors() {
        assertTrue(Hand.ROCK.beats(Hand.SCISSORS));
    }

    @Test
    void scissorsBeatsPaper() {
        assertTrue(Hand.SCISSORS.beats(Hand.PAPER));
    }

    @Test
    void paperBeatsRock() {
        assertTrue(Hand.PAPER.beats(Hand.ROCK));
    }

    @Test
    void sameHandDoesNotBeatItself() {
        // グー同士のような「同じ手」は勝敗が付かない(引き分け)ため、beats()はfalseを返す。
        assertFalse(Hand.ROCK.beats(Hand.ROCK));
    }

    @Test
    void losingHandDoesNotBeat() {
        // 負ける側の手(グー vs パー)でbeats()を呼んでも、勝ったことにはならない
        // (beats()は非対称なメソッドであることを確認する)。
        assertFalse(Hand.ROCK.beats(Hand.PAPER));
    }
}
