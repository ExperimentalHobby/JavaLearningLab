package com.javalab.quiz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionTest {

    @Test
    void isCorrectReturnsTrueForExactMatch() {
        Question question = new Question("Javaの生みの親は誰?", "James Gosling");

        assertTrue(question.isCorrect("James Gosling"));
    }

    @Test
    void isCorrectIgnoresWhitespaceAndCase() {
        Question question = new Question("Javaの生みの親は誰?", "James Gosling");

        assertTrue(question.isCorrect(" james gosling "));
    }

    @Test
    void isCorrectReturnsFalseForWrongAnswer() {
        Question question = new Question("Javaの生みの親は誰?", "James Gosling");

        assertFalse(question.isCorrect("wrong"));
    }
}
