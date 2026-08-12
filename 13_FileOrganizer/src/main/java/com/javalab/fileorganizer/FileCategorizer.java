package com.javalab.fileorganizer;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * ファイルの拡張子から整理先カテゴリを判定する。
 */
public class FileCategorizer {

    private static final Map<String, Set<String>> CATEGORY_EXTENSIONS = Map.of(
            "images", Set.of("jpg", "jpeg", "png", "gif"),
            "text", Set.of("txt", "md"),
            "documents", Set.of("pdf", "doc", "docx"));
    private static final String DEFAULT_CATEGORY = "others";

    /**
     * @param file 判定対象のファイルパス(実在しなくてもファイル名のみで判定可能)
     * @return カテゴリ名("images"/"text"/"documents"のいずれにも一致しない場合は"others")
     */
    public static String categoryOf(Path file) {
        String extension = extensionOf(file);
        return CATEGORY_EXTENSIONS.entrySet().stream()
                .filter(entry -> entry.getValue().contains(extension))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(DEFAULT_CATEGORY);
    }

    private static String extensionOf(Path file) {
        String fileName = file.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }
}
