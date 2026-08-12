package com.javalab.rpstournament;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * シングルエリミネーション方式のじゃんけんトーナメント。
 * 各ラウンドで{@code List<Player>}を先頭から2人ずつペアリングし、勝者だけを次ラウンドに残すことを
 * 参加者が1人になるまで繰り返す。
 */
public class Tournament {

    private final List<Player> players;

    /**
     * @param players 参加者一覧
     * @throws TournamentException 人数が2以上かつ2のべき乗でない場合(トーナメント表を組めないため)
     */
    public Tournament(List<Player> players) {
        if (!isPowerOfTwo(players.size())) {
            throw new TournamentException("参加人数は2以上かつ2のべき乗である必要があります: " + players.size());
        }
        this.players = players;
    }

    private static boolean isPowerOfTwo(int n) {
        return n >= 2 && (n & (n - 1)) == 0;
    }

    /**
     * 1ラウンド分の対戦を行う。リストの先頭から2人ずつペアにして対戦させ、勝者のリストを返す。
     * あいこの場合は{@code handSupplier}を再度呼び出して決着がつくまで再戦する。
     * @param currentRound このラウンドの参加者(偶数人数であること)
     * @param handSupplier プレイヤーの手を決定する処理(本番では乱数、テストでは固定値)
     * @return 各対戦の勝者のリスト(次ラウンドの参加者)
     */
    public List<Player> playRound(List<Player> currentRound, Function<Player, Hand> handSupplier) {
        List<Player> winners = new ArrayList<>();
        for (int i = 0; i < currentRound.size(); i += 2) {
            Player playerA = currentRound.get(i);
            Player playerB = currentRound.get(i + 1);
            Player winner = null;
            while (winner == null) {
                Hand handA = handSupplier.apply(playerA);
                Hand handB = handSupplier.apply(playerB);
                winner = RockPaperScissors.playMatch(playerA, handA, playerB, handB);
            }
            winners.add(winner);
        }
        return winners;
    }

    /**
     * 参加者が1人になるまで{@link #playRound}を繰り返し、優勝者を決定する。
     * @param handSupplier プレイヤーの手を決定する処理
     * @return 優勝者
     */
    public Player runTournament(Function<Player, Hand> handSupplier) {
        List<Player> currentRound = players;
        while (currentRound.size() > 1) {
            currentRound = playRound(currentRound, handSupplier);
        }
        return currentRound.get(0);
    }
}
