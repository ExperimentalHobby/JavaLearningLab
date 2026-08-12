package com.javalab.rpstournament;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        Player winner = RockPaperScissors.playMatch(playerA, Hand.ROCK, playerB, Hand.ROCK);

        assertNull(winner);
    }
}
