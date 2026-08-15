package com.javalab.mavenclipackaging;

/**
 * テキストから行数・単語数・文字数を計算する。
 */
public class TextStatisticsCalculator {

    /**
     * @param content 計算対象のテキスト
     * @return 行数・単語数・文字数
     */
    public TextStatistics calculate(String content) {
        if (content.isEmpty()) {
            return new TextStatistics(0, 0, 0);
        }
        int lines = content.split("\n", -1).length;
        int words = content.trim().isEmpty() ? 0 : content.trim().split("\\s+").length;
        int chars = content.length();
        return new TextStatistics(lines, words, chars);
    }
}
