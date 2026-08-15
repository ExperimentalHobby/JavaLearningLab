package com.javalab.gameoflife;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link GameOfLife#nextGeneration(Grid)} のConwayルール(過疎・過密・誕生・生存継続)を
 * それぞれ個別に検証し、最後に「ブリンカー」という既知の周期パターンで統合的に確認するテスト。
 */
class GameOfLifeTest {

    private final GameOfLife gameOfLife = new GameOfLife();

    @Test
    void liveCellWithTwoNeighborsSurvives() {
        // 中央(1,1)は生存セルで、隣接する生存マスがちょうど2つ(過疎・過密のどちらでもない)
        // なので次世代も生存し続けることを確認する。
        Grid grid = new Grid(3, 3);
        grid.setAlive(1, 1, true);
        grid.setAlive(0, 0, true);
        grid.setAlive(0, 1, true);

        Grid next = gameOfLife.nextGeneration(grid);

        assertTrue(next.isAlive(1, 1));
    }

    @Test
    void liveCellWithFewerThanTwoNeighborsDiesFromUnderpopulation() {
        // 隣接する生存マスが1つしかない(過疎)ため、次世代で死滅することを確認する。
        Grid grid = new Grid(3, 3);
        grid.setAlive(1, 1, true);
        grid.setAlive(0, 0, true);

        Grid next = gameOfLife.nextGeneration(grid);

        assertFalse(next.isAlive(1, 1));
    }

    @Test
    void liveCellWithMoreThanThreeNeighborsDiesFromOverpopulation() {
        // 隣接する生存マスが4つ(過密)のため、次世代で死滅することを確認する。
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
        // 中央(1,1)は死セルだが、隣接する生存マスがちょうど3つのため、次世代で誕生することを確認する。
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
