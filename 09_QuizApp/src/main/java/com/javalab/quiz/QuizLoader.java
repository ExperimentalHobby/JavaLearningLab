package com.javalab.quiz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * java.util.Propertiesを使って、問題データをプロパティファイルから読み込む。
 * 形式: {@code question.count} に問題数、{@code q<N>.question}/{@code q<N>.answer} に各問の内容を記述する。
 */
public class QuizLoader {

    /**
     * ファイルシステム上のプロパティファイルから問題一覧を読み込む。
     * @param file 読み込み元ファイル
     * @return 問題一覧
     * @throws IOException ファイル読み込みに失敗した場合
     * @throws QuizLoaderException question.countや各問のquestion/answerキーが欠落している場合
     */
    public static List<Question> loadFromProperties(File file) throws IOException {
        try (InputStreamReader reader =
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return loadFromReader(reader, file.toString());
        }
    }

    /**
     * クラスパス上のプロパティファイルから問題一覧を読み込む(本番実行時、jar内蔵のデフォルト問題用)。
     * @param resourcePath クラスパス上のリソースパス(例: "/questions.properties")
     * @return 問題一覧
     * @throws IOException リソース読み込みに失敗した場合
     * @throws QuizLoaderException リソースが見つからない、または内容が不正な場合
     */
    public static List<Question> loadFromClasspath(String resourcePath) throws IOException {
        try (InputStream input = QuizLoader.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new QuizLoaderException("クラスパス上に見つかりません: " + resourcePath);
            }
            return loadFromReader(new InputStreamReader(input, StandardCharsets.UTF_8), resourcePath);
        }
    }

    private static List<Question> loadFromReader(InputStreamReader reader, String source) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);

        String countText = properties.getProperty("question.count");
        if (countText == null) {
            throw new QuizLoaderException("question.count が見つかりません: " + source);
        }
        int count = Integer.parseInt(countText);

        List<Question> questions = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String text = properties.getProperty("q" + i + ".question");
            String answer = properties.getProperty("q" + i + ".answer");
            if (text == null || answer == null) {
                throw new QuizLoaderException("q" + i + " のquestion/answerが見つかりません: " + source);
            }
            questions.add(new Question(text, answer));
        }
        return questions;
    }
}
