package com.javalab.gameoflife;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GridTest {

    @Test
    void countLiveNeighborsCountsSurroundingCellsRespectingBounds() {
        Grid grid = new Grid(3, 3);
        // 中央(1,1)の周囲8マスのうち、左上(0,0)・右下(2,2)の2マスだけを生存させる。
        grid.setAlive(0, 0, true);
        grid.setAlive(2, 2, true);

        int count = grid.countLiveNeighbors(1, 1);

        assertEquals(2, count);
    }
}
