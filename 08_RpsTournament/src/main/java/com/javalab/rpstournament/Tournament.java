package com.javalab.rpstournament;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * シングルエリミネーション方式のじゃんけんトーナメント。
 * 各ラウンドで{@code List<Player>}を先頭から2人ずつペアリングし、勝者だけを次ラウンドに残すことを
 * 参加者が1人になるまで繰り返す。参加人数が奇数のラウンドでは最後の1人が不戦勝として次ラウンドへ進む。
 */
public class Tournament {

    // あいこの再戦に上限を設けないと、決定的に引き分けの手だけを返すhandSupplier
    // (テスト用スタブや、将来「常にグーを出す戦略AI」等)を渡した場合に無限ループしてしまう。
    private static final int MAX_REROLLS = 1000;

    private final List<Player> players;

    /**
     * @param players 参加者一覧
     * @throws TournamentException 人数が2未満の場合(対戦相手がいないため)
     */
    public Tournament(List<Player> players) {
        if (players.size() < 2) {
            throw new TournamentException("参加人数は2人以上である必要があります: " + players.size());
        }
        this.players = players;
    }

    /**
     * 1ラウンド分の対戦を行う。リストの先頭から2人ずつペアにして対戦させ、勝者のリストを返す。
     * あいこの場合は{@code handSupplier}を再度呼び出して決着がつくまで再戦する。
     * 参加者数が奇数の場合、最後の1人は不戦勝として結果に含まれる。
     * @param currentRound このラウンドの参加者
     * @param handSupplier プレイヤーの手を決定する処理(本番では乱数、テストでは固定値)
     * @return 各対戦の勝者のリスト(次ラウンドの参加者)
     */
    public List<Player> playRound(List<Player> currentRound, Function<Player, Hand> handSupplier) {
        return playRoundWithResults(currentRound, handSupplier).stream()
                .map(MatchResult::winner)
                .toList();
    }

    /**
     * {@link #playRound}と同様に1ラウンド分の対戦を行うが、勝者だけでなく対戦カード
     * (誰と誰が戦い、どちらが勝ったか)も含む{@link MatchResult}のリストを返す。
     * @param currentRound このラウンドの参加者
     * @param handSupplier プレイヤーの手を決定する処理
     * @return 各対戦の結果のリスト
     */
    public List<MatchResult> playRoundWithResults(List<Player> currentRound, Function<Player, Hand> handSupplier) {
        List<MatchResult> results = new ArrayList<>();
        int pairedCount = currentRound.size() - (currentRound.size() % 2);
        for (int i = 0; i < pairedCount; i += 2) {
            Player playerA = currentRound.get(i);
            Player playerB = currentRound.get(i + 1);
            Player winner = null;
            int rerolls = 0;
            while (winner == null) {
                if (rerolls >= MAX_REROLLS) {
                    throw new TournamentException(
                            "あいこが" + MAX_REROLLS + "回続いたため対戦を中断しました: "
                                    + playerA.getName() + " vs " + playerB.getName());
                }
                Hand handA = handSupplier.apply(playerA);
                Hand handB = handSupplier.apply(playerB);
                winner = RockPaperScissors.playMatch(playerA, handA, playerB, handB);
                rerolls++;
            }
            results.add(new MatchResult(playerA, playerB, winner));
        }
        if (currentRound.size() % 2 != 0) {
            // 参加者数が奇数の場合、最後の1人は対戦相手がいないため不戦勝とする。
            Player byePlayer = currentRound.get(currentRound.size() - 1);
            results.add(new MatchResult(byePlayer, null, byePlayer));
        }
        return results;
    }

    /**
     * 参加者が1人になるまで{@link #playRound}を繰り返し、優勝者を決定する。
     * @param handSupplier プレイヤーの手を決定する処理
     * @return 優勝者
     */
    public Player runTournament(Function<Player, Hand> handSupplier) {
        return runTournament(handSupplier, results -> { });
    }

    /**
     * {@link #runTournament(Function)}と同様だが、各ラウンドの対戦結果を{@code roundReporter}へ
     * 通知する。大会の進行(誰と誰が戦い、どちらが勝ったか)を表示したい呼び出し側向け。
     * @param handSupplier プレイヤーの手を決定する処理
     * @param roundReporter 1ラウンド分の対戦結果を受け取るコールバック
     * @return 優勝者
     */
    public Player runTournament(Function<Player, Hand> handSupplier, Consumer<List<MatchResult>> roundReporter) {
        List<Player> currentRound = players;
        while (currentRound.size() > 1) {
            List<MatchResult> results = playRoundWithResults(currentRound, handSupplier);
            roundReporter.accept(results);
            currentRound = results.stream().map(MatchResult::winner).toList();
        }
        return currentRound.get(0);
    }

    /**
     * 1試合分の対戦結果。{@code playerB}が{@code null}の場合は{@code playerA}の不戦勝を表す。
     * @param playerA 対戦者A(不戦勝の場合は勝者本人)
     * @param playerB 対戦者B(不戦勝の場合はnull)
     * @param winner 勝者
     */
    public record MatchResult(Player playerA, Player playerB, Player winner) {
    }
}
