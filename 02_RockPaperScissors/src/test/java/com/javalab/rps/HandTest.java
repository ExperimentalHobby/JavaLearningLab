package com.javalab.rps;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertFalse(Hand.ROCK.beats(Hand.ROCK));
    }

    @Test
    void losingHandDoesNotBeat() {
        assertFalse(Hand.ROCK.beats(Hand.PAPER));
    }
}
