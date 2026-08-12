package com.javalab.rpstournament;

/**
 * 不正な参加人数・不明なコマンドなど、トーナメント運営上のエラーを表す例外。
 */
public class TournamentException extends RuntimeException {

    /**
     * @param message エラー内容を説明するメッセージ
     */
    public TournamentException(String message) {
        super(message);
    }
}
