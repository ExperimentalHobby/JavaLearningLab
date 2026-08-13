package com.javalab.gameoflife;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameOfLifeTest {

    private final GameOfLife gameOfLife = new GameOfLife();

    @Test
    void liveCellWithTwoNeighborsSurvives() {
        Grid grid = new Grid(3, 3);
        grid.setAlive(1, 1, true);
        grid.setAlive(0, 0, true);
        grid.setAlive(0, 1, true);

        Grid next = gameOfLife.nextGeneration(grid);

        assertTrue(next.isAlive(1, 1));
    }

    @Test
    void liveCellWithFewerThanTwoNeighborsDiesFromUnderpopulation() {
        Grid grid = new Grid(3, 3);
        grid.setAlive(1, 1, true);
        grid.setAlive(0, 0, true);

        Grid next = gameOfLife.nextGeneration(grid);

        assertFalse(next.isAlive(1, 1));
    }

    @Test
    void liveCellWithMoreThanThreeNeighborsDiesFromOverpopulation() {
        Grid grid = new Grid(3, 3);
        grid.setAlive(1, 1, true);
        grid.setAlive(0, 0, true);
        grid.setAlive(0, 1, true);
        grid.setAlive(0, 2, true);
        grid.setAlive(1, 0, true);

        Grid next = gameOfLife.nextGeneration(grid);

        assertFalse(next.isAlive(1, 1));
    }

    @Test
    void deadCellWithExactlyThreeNeighborsBecomesAlive() {
        Grid grid = new Grid(3, 3);
        grid.setAlive(0, 0, true);
        grid.setAlive(0, 1, true);
        grid.setAlive(0, 2, true);

        Grid next = gameOfLife.nextGeneration(grid);

        assertTrue(next.isAlive(1, 1));
    }

    @Test
    void blinkerRotatesNinetyDegreesAfterOneGeneration() {
        // 「ブリンカー」: 3マスの水平な直線は1世代で垂直な直線に回転する既知の周期パターン。
        Grid grid = new Grid(3, 3);
        grid.setAlive(1, 0, true);
        grid.setAlive(1, 1, true);
        grid.setAlive(1, 2, true);

        Grid next = gameOfLife.nextGeneration(grid);

        assertTrue(next.isAlive(0, 1));
        assertTrue(next.isAlive(1, 1));
        assertTrue(next.isAlive(2, 1));
        assertFalse(next.isAlive(1, 0));
        assertFalse(next.isAlive(1, 2));
    }
}
