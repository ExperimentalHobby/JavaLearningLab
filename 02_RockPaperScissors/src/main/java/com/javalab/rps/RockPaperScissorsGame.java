package com.javalab.rps;

import java.util.Random;

/**
 * じゃんけんの手の抽選と勝敗判定を行うロジック。
 */
public class RockPaperScissorsGame {

    /**
     * コンピュータの手を乱数で決定する。
     * {@link Random} を引数で受け取ることで、テストでは固定値を返すサブクラスを注入し、
     * 決定的に検証できるようにしている。
     * @param random 乱数生成器
     * @return ランダムに選ばれた手
     */
    public static Hand randomHand(Random random) {
        return Hand.values()[random.nextInt(Hand.values().length)];
    }

    /**
     * プレイヤーとコンピュータの手から勝敗を判定する。
     * @param player プレイヤーの手
     * @param computer コンピュータの手
     * @return プレイヤー視点の勝敗結果
     */
    public static Result judge(Hand player, Hand computer) {
        if (player.beats(computer)) {
            return Result.PLAYER_WIN;
        }
        if (computer.beats(player)) {
            return Result.COMPUTER_WIN;
        }
        return Result.DRAW;
    }
}
