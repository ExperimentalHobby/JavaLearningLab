package com.javalab.quiz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreManagerTest {

    private final ScoreManager scoreManager = new ScoreManager();

    @Test
    void recordAnswerCountsCorrectAndTotal() {
        scoreManager.recordAnswer(true);
        scoreManager.recordAnswer(false);

        assertEquals(1, scoreManager.getScore());
        assertEquals(2, scoreManager.getTotal());
    }

    @Test
    void getPercentageCalculatesCorrectRatio() {
        scoreManager.recordAnswer(true);
        scoreManager.recordAnswer(false);

        assertEquals(50.0, scoreManager.getPercentage());
    }

    @Test
    void getPercentageReturnsZeroWhenNoQuestionsAnswered() {
        assertEquals(0.0, scoreManager.getPercentage());
    }
}
