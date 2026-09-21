package com.javalab.gameoflife;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Grid#countLiveNeighbors(int, int)} の隣接カウントロジックを検証するテスト。
 */
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

    @Test
    void countLiveNeighborsWithWrapAroundCountsCellsAcrossOppositeEdges() {
        // トーラスモード: 端が反対側と繋がっているとみなす。
        // (0,0)の隣接マスとして、右端(0,2)・下端(2,0)・右下端(2,2)も数えられるべき。
        Grid grid = new Grid(3, 3);
        grid.setAlive(0, 2, true);
        grid.setAlive(2, 0, true);
        grid.setAlive(2, 2, true);

        int count = grid.countLiveNeighbors(0, 0, true);

        assertEquals(3, count);
    }

    @Test
    void countLiveNeighborsWithoutWrapAroundIgnoresOppositeEdges() {
        Grid grid = new Grid(3, 3);
        grid.setAlive(0, 2, true);
        grid.setAlive(2, 0, true);
        grid.setAlive(2, 2, true);

        int count = grid.countLiveNeighbors(0, 0, false);

        assertEquals(0, count);
    }

    @Test
    void isAliveThrowsIllegalArgumentExceptionForOutOfRangeRow() {
        // 配列アクセスに任せるとArrayIndexOutOfBoundsExceptionにはなるが、
        // 「呼び出し側が範囲を守る」という暗黙の前提に依存した危うい公開APIだったため、
        // 明示的な検証でIllegalArgumentExceptionを送出するようにする。
        Grid grid = new Grid(3, 3);

        assertThrows(IllegalArgumentException.class, () -> grid.isAlive(3, 0));
    }

    @Test
    void setAliveThrowsIllegalArgumentExceptionForOutOfRangeCol() {
        Grid grid = new Grid(3, 3);

        assertThrows(IllegalArgumentException.class, () -> grid.setAlive(0, -1, true));
    }

    @Test
    void equalsReturnsTrueForGridsWithSameSizeAndSameLiveCells() {
        Grid a = new Grid(3, 3);
        a.setAlive(0, 0, true);
        a.setAlive(1, 1, true);
        Grid b = new Grid(3, 3);
        b.setAlive(0, 0, true);
        b.setAlive(1, 1, true);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsReturnsFalseForGridsWithDifferentLiveCells() {
        Grid a = new Grid(3, 3);
        a.setAlive(0, 0, true);
        Grid b = new Grid(3, 3);
        b.setAlive(0, 1, true);

        assertNotEquals(a, b);
    }
}
