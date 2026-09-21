package com.javalab.mazesolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * グリッド状の迷路を表す。マス同士の接続(壁のない通路)を持つ。
 * 生成アルゴリズム({@link MazeGenerator})が{@link #connect(Cell, Cell)}で組み立てる可変オブジェクト。
 */
public class Maze {

    private final int width;
    private final int height;
    private final Map<Cell, Set<Cell>> passages = new HashMap<>();
    private final Cell start;
    private final Cell goal;

    public Maze(int width, int height, Cell start, Cell goal) {
        this.width = width;
        this.height = height;
        this.start = start;
        this.goal = goal;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Cell start() {
        return start;
    }

    public Cell goal() {
        return goal;
    }

    /**
     * 2マス間の壁を取り払い、通路で接続する(双方向)。
     */
    public void connect(Cell a, Cell b) {
        passages.computeIfAbsent(a, k -> new HashSet<>()).add(b);
        passages.computeIfAbsent(b, k -> new HashSet<>()).add(a);
    }

    public boolean isConnected(Cell a, Cell b) {
        return passages.getOrDefault(a, Set.of()).contains(b);
    }

    /**
     * 指定マスから壁を越えずに直接移動できる隣接マスの集合を返す。
     */
    public Set<Cell> openNeighbors(Cell cell) {
        return passages.getOrDefault(cell, Set.of());
    }

    /**
     * マス間の接続状態を比較用に取得する(テストでの構造比較に使用)。
     * {@code Map.copyOf}だけでは値である{@code Set<Cell>}が可変の実体のまま共有されてしまうため、
     * 値のSetも{@code Set.copyOf}で防御的コピーし、呼び出し側から内部状態を変更できないようにする。
     */
    public Map<Cell, Set<Cell>> passages() {
        return passages.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> Set.copyOf(e.getValue())));
    }
}
