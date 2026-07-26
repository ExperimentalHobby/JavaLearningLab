package com.javalab.rps;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private static Random fixedRandom(int value) {
        return new Random() {
            @Override
            public int nextInt(int bound) {
                return value;
            }
        };
    }
}
