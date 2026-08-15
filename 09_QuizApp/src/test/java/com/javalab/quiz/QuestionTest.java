package com.javalab.quiz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link Question#isCorrect(String)} の正誤判定ロジックを検証するテスト。
 * 完全一致だけでなく、ユーザーが入力しがちな表記ゆれ(前後の空白・大文字小文字)を
 * 許容することも仕様として確認する。
 */
class QuestionTest {

    @Test
    void isCorrectReturnsTrueForExactMatch() {
        Question question = new Question("Javaの生みの親は誰?", "James Gosling");

        assertTrue(question.isCorrect("James Gosling"));
    }

    @Test
    void isCorrectIgnoresWhitespaceAndCase() {
        // " james gosling " のように前後の空白・小文字化されていても正解として扱う。
        Question question = new Question("Javaの生みの親は誰?", "James Gosling");

        assertTrue(question.isCorrect(" james gosling "));
    }

    @Test
    void isCorrectReturnsFalseForWrongAnswer() {
        Question question = new Question("Javaの生みの親は誰?", "James Gosling");

        assertFalse(question.isCorrect("wrong"));
    }
}
