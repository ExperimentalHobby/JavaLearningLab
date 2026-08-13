package com.javalab.gameoflife;

/**
 * Conwayのライフゲームのルールに従い、盤面を次の世代へ進める純粋なロジックを提供するクラス。
 */
public class GameOfLife {

    /**
     * 現在の盤面から次の世代の盤面を生成する。
     * ルール: 生存セルは隣接する生存マスが2つか3つなら生存継続、それ以外は死滅。
     * 死セルは隣接する生存マスがちょうど3つなら誕生する。
     * @param current 現在の盤面
     * @return 次の世代の盤面(新しい{@link Grid}インスタンス)
     */
    public Grid nextGeneration(Grid current) {
        Grid next = new Grid(current.width(), current.height());
        for (int row = 0; row < current.height(); row++) {
            for (int col = 0; col < current.width(); col++) {
                int liveNeighbors = current.countLiveNeighbors(row, col);
                boolean alive = current.isAlive(row, col);
                boolean nextAlive = (alive && (liveNeighbors == 2 || liveNeighbors == 3))
                        || (!alive && liveNeighbors == 3);
                next.setAlive(row, col, nextAlive);
            }
        }
        return next;
    }
}
