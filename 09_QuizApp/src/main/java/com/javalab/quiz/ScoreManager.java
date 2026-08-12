package com.javalab.quiz;

/**
 * クイズの出題数・正解数を集計する。
 */
public class ScoreManager {

    private int score;
    private int total;

    /**
     * 1問分の結果を記録する。
     * @param correct 正解したかどうか
     */
    public void recordAnswer(boolean correct) {
        total++;
        if (correct) {
            score++;
        }
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }

    /**
     * @return 正答率(%)。出題数が0の場合はゼロ除算を避けて0.0を返す
     */
    public double getPercentage() {
        if (total == 0) {
            return 0.0;
        }
        return score * 100.0 / total;
    }
}
