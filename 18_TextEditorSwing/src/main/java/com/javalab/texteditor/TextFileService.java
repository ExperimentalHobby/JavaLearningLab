package com.javalab.texteditor;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * テキストファイルの読み込み・書き込みを担うGUIに依存しないロジッククラス。
 * ファイルI/O関連の失敗は{@link TextFileException}に統一して呼び出し元へ伝える。
 */
public class TextFileService {

    /**
     * 指定パスのファイル内容をUTF-8の文字列として読み込む。
     * @param path 読み込み対象のパス
     * @return ファイル内容
     * @throws TextFileException 読み込みに失敗した場合(ファイルが存在しない場合を含む)
     */
    public String load(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            // UTF-8として不正なバイト列(例: Shift_JISで保存されたファイル)の場合、
            // 「ファイルの読み込みに失敗しました」とだけ出ても原因が分からなかったため、
            // 文字コードが原因である可能性を明示する。
            throw new TextFileException(
                    "ファイルの文字コードが不正です(UTF-8以外で保存されている可能性があります): " + path, e);
        } catch (IOException e) {
            throw new TextFileException("ファイルの読み込みに失敗しました: " + path, e);
        }
    }

    /**
     * 指定パスへ文字列をUTF-8で書き込む。
     * @param path 書き込み先のパス
     * @param content 書き込む内容
     * @throws TextFileException 書き込みに失敗した場合(親ディレクトリが存在しない場合を含む)
     */
    public void save(Path path, String content) {
        try {
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TextFileException("ファイルの保存に失敗しました: " + path, e);
        }
    }
}
