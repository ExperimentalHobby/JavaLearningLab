package com.javalab.quiz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link ScoreManager} の正解数・出題数の集計と正答率計算を検証するテスト。
 */
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
        // 1問も回答していない状態(total=0)でgetPercentage()を呼んでも、
        // ゼロ除算(0/0)にならず0.0を返す安全策が効いていることを確認する。
        assertEquals(0.0, scoreManager.getPercentage());
    }
}
