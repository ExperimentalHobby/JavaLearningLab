package com.javalab.rpstournament;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Tournament} のシングルエリミネーション方式のトーナメント進行を検証するテスト。
 * 参加人数バリデーション、1ラウンド分のペアリング、あいこ時の再戦、
 * 複数ラウンドを経て優勝者が1人に絞り込まれる流れを確認する。
 */
class TournamentTest {

    private final Player alice = new Player("Alice");
    private final Player bob = new Player("Bob");
    private final Player carol = new Player("Carol");
    private final Player dave = new Player("Dave");

    @Test
    void constructorThrowsExceptionWhenPlayerCountIsNotPowerOfTwo() {
        // 3人はトーナメント表を組めない(誰かが不戦勝になってしまう)人数のため、
        // コンストラクタの時点でTournamentExceptionをスローする。
        List<Player> players = List.of(new Player("Alice"), new Player("Bob"), new Player("Carol"));

        assertThrows(TournamentException.class, () -> new Tournament(players));
    }

    @Test
    void constructorThrowsExceptionWhenOnlyOnePlayer() {
        // 1人は「2以上」の条件を満たさないため不正(そもそも対戦相手がいない)。
        List<Player> players = List.of(new Player("Alice"));

        assertThrows(TournamentException.class, () -> new Tournament(players));
    }

    @Test
    void playRoundPairsAdjacentPlayersAndReturnsWinners() {
        // リストの先頭から2人ずつ(alice,bob)(carol,dave)がペアになり、
        // それぞれの勝者(alice, carol)だけが返り値のリストに残ることを確認する。
        List<Player> players = List.of(alice, bob, carol, dave);
        Tournament tournament = new Tournament(players);
        Map<Player, Hand> hands = Map.of(
                alice, Hand.ROCK, bob, Hand.SCISSORS,
                carol, Hand.PAPER, dave, Hand.ROCK);

        List<Player> winners = tournament.playRound(players, hands::get);

        assertEquals(List.of(alice, carol), winners);
    }

    @Test
    void playRoundRerollsOnDrawUntilDecisive() {
        // 1回目はalice=ROCK, bob=ROCKで引き分けになるよう手を仕込み、
        // playRound内部で決着がつくまで再戦(while (winner == null)ループ)が行われ、
        // 2回目の手(alice=PAPER, bob=SCISSORS → bobの勝ち)で最終的に決着することを確認する。
        // Dequeを使い「1回目の手」「2回目の手」を順番に取り出せるようにしている。
        List<Player> players = List.of(alice, bob);
        Tournament tournament = new Tournament(List.of(alice, bob, carol, dave));
        Deque<Hand> aliceHands = new ArrayDeque<>(List.of(Hand.ROCK, Hand.PAPER));
        Deque<Hand> bobHands = new ArrayDeque<>(List.of(Hand.ROCK, Hand.SCISSORS));

        List<Player> winners = tournament.playRound(players, player ->
                player == alice ? aliceHands.poll() : bobHands.poll());

        assertEquals(List.of(bob), winners);
    }

    @Test
    void runTournamentNarrowsDownToSingleChampion() {
        // 1回戦: alice vs bob → alice、carol vs dave → carol
        // 決勝: alice vs carol → carol(優勝)
        // 複数ラウンドをまたいでも、最終的に参加者が1人になるまでplayRound()が
        // 繰り返し呼ばれることを確認する(runTournament()自体は再帰ではなくwhileループで実装)。
        Tournament tournament = new Tournament(List.of(alice, bob, carol, dave));
        Map<Player, Hand> hands = Map.of(
                alice, Hand.ROCK, bob, Hand.SCISSORS,
                carol, Hand.PAPER, dave, Hand.ROCK);

        Player champion = tournament.runTournament(hands::get);

        assertEquals(carol, champion);
    }
}
