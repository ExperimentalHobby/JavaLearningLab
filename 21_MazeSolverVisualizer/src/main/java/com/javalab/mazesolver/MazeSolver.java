package com.javalab.mazesolver;

import java.util.List;

/**
 * 迷路の探索アルゴリズムを表すインターフェース。
 */
public interface MazeSolver {

    /**
     * スタートからゴールまでの経路を探索する。
     * @param maze 探索対象の迷路
     * @return スタートからゴールまでの経路(両端を含む)。到達できない場合は空リスト
     */
    List<Cell> solve(Maze maze);
}
