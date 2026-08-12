package com.javalab.quiz;

/**
 * クイズの1問(問題文と正解)。
 */
public class Question {

    private final String text;
    private final String answer;

    public Question(String text, String answer) {
        this.text = text;
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    /**
     * ユーザーの回答が正解かを判定する。前後の空白・大文字小文字の違いは無視する。
     * @param userAnswer ユーザーが入力した回答
     * @return 正解の場合true
     */
    public boolean isCorrect(String userAnswer) {
        return answer.trim().equalsIgnoreCase(userAnswer.trim());
    }
}
