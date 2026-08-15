package com.javalab.mazesolver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link MazeVisualizerState} のアルゴリズム選択・アニメーション状態遷移を検証するクラス。
 * {@link javafx.animation.Timeline}・{@code Canvas} 等のJavaFX描画要素には依存せず、
 * 状態遷移ロジックのみを検証する。
 */
class MazeVisualizerStateTest {

    private final Map<String, MazeSolver> solvers = Map.of(
            "BFS", new BfsMazeSolver(),
            "DFS", new DfsMazeSolver(),
            "A*", new AStarMazeSolver());

    private MazeVisualizerState state;

    @BeforeEach
    void setUp() {
        state = new MazeVisualizerState(new MazeGenerator(), solvers);
        state.generateMaze(5, 5, 42L);
    }

    @Test
    void isAnimatingIsFalseInitially() {
        assertFalse(state.isAnimating());
    }

    @Test
    void solverForReturnsCorrectSolverInstanceForEachAlgorithmName() {
        assertInstanceOf(BfsMazeSolver.class, state.solverFor("BFS"));
        assertInstanceOf(DfsMazeSolver.class, state.solverFor("DFS"));
        assertInstanceOf(AStarMazeSolver.class, state.solverFor("A*"));
    }

    @Test
    void solverForThrowsIllegalArgumentExceptionForUnknownName() {
        assertThrows(IllegalArgumentException.class, () -> state.solverFor("greedy"));
    }

    @Test
    void startAnimationReturnsSolverPathAndMarksAnimating() {
        List<Cell> expectedPath = new BfsMazeSolver().solve(state.currentMaze());

        List<Cell> actualPath = state.startAnimation("BFS");

        assertEquals(expectedPath, actualPath);
        assertTrue(state.isAnimating());
    }

    @Test
    void generateMazeResetsAnimatingStateEvenDuringAnimation() {
        state.startAnimation("BFS");
        assertTrue(state.isAnimating());

        state.generateMaze(5, 5, 99L);

        assertFalse(state.isAnimating());
    }
}
