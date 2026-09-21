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
        // 改行をLF(\n)に正規化する。CRLFの\rが文字数に含まれたり行末に残ったりしないようにするため。
        String normalized = content.replace("\r\n", "\n").replace("\r", "\n");
        // wc -lと同じく「改行文字の出現回数」を行数とする。split("\n", -1).lengthだと
        // 末尾が改行で終わる通常のテキストファイルで末尾の空要素が数に入り1多くなってしまう。
        int lines = (int) normalized.chars().filter(c -> c == '\n').count();
        int words = normalized.trim().isEmpty() ? 0 : normalized.trim().split("\\s+").length;
        int chars = normalized.length();
        return new TextStatistics(lines, words, chars);
    }
}
