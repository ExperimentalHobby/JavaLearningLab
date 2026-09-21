package com.javalab.rpstournament;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * {@link Player} の同一性({@code equals}/{@code hashCode})を検証するテスト。
 */
class PlayerTest {

    @Test
    void playersWithSameNameAreEqual() {
        // 同名プレイヤーの重複登録を防ぐには、まず名前ベースの同一性が必要となる。
        Player player1 = new Player("Alice");
        Player player2 = new Player("Alice");

        assertEquals(player1, player2);
        assertEquals(player1.hashCode(), player2.hashCode());
    }

    @Test
    void playersWithDifferentNamesAreNotEqual() {
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");

        assertNotEquals(player1, player2);
    }
}
