package com.javalab.gameoflife;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link LifeBoardState} の盤面編集・世代進行・クリア・ランダム生成という状態遷移を検証するクラス。
 * {@link javax.swing.Timer}・{@code JPanel} 等のSwing描画要素には依存せず、状態遷移ロジックのみを検証する。
 */
class LifeBoardStateTest {

    private LifeBoardState state;

    @BeforeEach
    void setUp() {
        state = new LifeBoardState(3, 3, new GameOfLife(), new Random(42L));
    }

    @Test
    void toggleCellFlipsAliveState() {
        assertFalse(state.grid().isAlive(1, 1));

        state.toggleCell(1, 1);

        assertTrue(state.grid().isAlive(1, 1));

        state.toggleCell(1, 1);

        assertFalse(state.grid().isAlive(1, 1));
    }

    @Test
    void stepIncrementsGenerationAndAppliesNextGenerationRule() {
        // 「ブリンカー」: 3マスの水平な直線は1世代で垂直な直線に回転する既知の周期パターン
        // (GameOfLifeTestと同じパターンを使い、状態遷移(世代カウンタ)側の責務を検証する)。
        state.toggleCell(1, 0);
        state.toggleCell(1, 1);
        state.toggleCell(1, 2);

        state.step();

        assertEquals(1, state.generation());
        assertTrue(state.grid().isAlive(0, 1));
        assertTrue(state.grid().isAlive(1, 1));
        assertTrue(state.grid().isAlive(2, 1));
        assertFalse(state.grid().isAlive(1, 0));
        assertFalse(state.grid().isAlive(1, 2));
    }

    @Test
    void clearResetsGridToAllDeadAndGenerationToZero() {
        state.toggleCell(1, 0);
        state.toggleCell(1, 1);
        state.toggleCell(1, 2);
        state.step();

        state.clear();

        assertEquals(0, state.generation());
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                assertFalse(state.grid().isAlive(row, col));
            }
        }
    }

    @Test
    void randomizeProducesReproducibleGridForSameSeed() {
        // 同じシードのRandomを注入すれば、乱数の消費順序が同一になり
        // 全く同じ盤面が再現されることを確認する(MazeGeneratorTestと同じ考え方)。
        LifeBoardState first = new LifeBoardState(3, 3, new GameOfLife(), new Random(7L));
        LifeBoardState second = new LifeBoardState(3, 3, new GameOfLife(), new Random(7L));

        first.randomize();
        second.randomize();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                assertEquals(first.grid().isAlive(row, col), second.grid().isAlive(row, col));
            }
        }
    }

    @Test
    void randomizeResetsGenerationToZero() {
        state.toggleCell(1, 0);
        state.toggleCell(1, 1);
        state.toggleCell(1, 2);
        state.step();
        assertEquals(1, state.generation());

        state.randomize();

        assertEquals(0, state.generation());
    }
}
