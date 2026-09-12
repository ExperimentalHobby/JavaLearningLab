package com.javalab.jmhbenchmark;

import java.util.List;

/**
 * 文字列結合方式の比較対象となるロジック。JMHベンチマークメソッドに直接書かず、
 * 独立したユーティリティとして切り出すことで、まずJUnitで正しさを検証できるようにしている。
 */
public final class StringConcatUtil {

    private StringConcatUtil() {
    }

    /** {@code +}演算子で連結する(文字列は不変のため、連結のたびに新しいインスタンスが生成される)。 */
    public static String concatWithPlusOperator(List<String> parts) {
        String result = "";
        for (String part : parts) {
            result += part;
        }
        return result;
    }

    /** {@link StringBuilder}で連結する(内部バッファへ追記するため再生成が発生しない)。 */
    public static String concatWithStringBuilder(List<String> parts) {
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            builder.append(part);
        }
        return builder.toString();
    }
}
