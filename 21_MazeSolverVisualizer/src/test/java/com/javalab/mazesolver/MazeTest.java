package com.javalab.mazesolver;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link Maze} の通路管理を検証するクラス。
 */
class MazeTest {

    @Test
    void passagesReturnsUnmodifiableSetsSoInternalStateCannotBeMutatedByCaller() {
        // Map.copyOf(passages)はマップ自体は防御的コピーされるが、値であるSet<Cell>は
        // 可変の実体がそのまま共有されるため、呼び出し側からSetを直接変更できてしまっていた問題。
        Cell a = new Cell(0, 0);
        Cell b = new Cell(0, 1);
        Maze maze = new Maze(2, 1, a, b);
        maze.connect(a, b);

        Set<Cell> neighborsOfA = maze.passages().get(a);

        assertThrows(UnsupportedOperationException.class, () -> neighborsOfA.add(new Cell(9, 9)));
    }
}
