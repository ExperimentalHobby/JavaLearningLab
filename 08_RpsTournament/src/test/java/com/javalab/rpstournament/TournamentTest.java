package com.javalab.rpstournament;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TournamentTest {

    private final Player alice = new Player("Alice");
    private final Player bob = new Player("Bob");
    private final Player carol = new Player("Carol");
    private final Player dave = new Player("Dave");

    @Test
    void constructorThrowsExceptionWhenPlayerCountIsNotPowerOfTwo() {
        List<Player> players = List.of(new Player("Alice"), new Player("Bob"), new Player("Carol"));

        assertThrows(TournamentException.class, () -> new Tournament(players));
    }

    @Test
    void constructorThrowsExceptionWhenOnlyOnePlayer() {
        List<Player> players = List.of(new Player("Alice"));

        assertThrows(TournamentException.class, () -> new Tournament(players));
    }

    @Test
    void playRoundPairsAdjacentPlayersAndReturnsWinners() {
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
        Tournament tournament = new Tournament(List.of(alice, bob, carol, dave));
        // 1回戦: alice vs bob → alice、carol vs dave → carol
        // 決勝: alice vs carol → carol(優勝)
        Map<Player, Hand> hands = Map.of(
                alice, Hand.ROCK, bob, Hand.SCISSORS,
                carol, Hand.PAPER, dave, Hand.ROCK);

        Player champion = tournament.runTournament(hands::get);

        assertEquals(carol, champion);
    }
}
