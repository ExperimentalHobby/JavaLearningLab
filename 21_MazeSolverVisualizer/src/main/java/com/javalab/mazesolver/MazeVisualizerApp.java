package com.javalab.mazesolver;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

/**
 * 迷路生成&探索ビジュアライザのメインウィンドウ。
 * GUIの描画・アニメーション自体は自動テスト対象外のため、実際にアプリを起動して手動確認している。
 * {@code main}メソッドを持つクラスが直接{@link Application}を継承していると、クラスパス実行時に
 * 「JavaFXランタイム・コンポーネントが不足しています」というエラーになるため、
 * エントリーポイントは{@link Main}に分離している。
 */
public class MazeVisualizerApp extends Application {

    private static final int COLS = 20;
    private static final int ROWS = 15;
    private static final int CELL_SIZE = 28;

    private final MazeGenerator generator = new MazeGenerator();
    private final Map<String, MazeSolver> solvers = Map.of(
            "BFS", new BfsMazeSolver(),
            "DFS", new DfsMazeSolver(),
            "A*", new AStarMazeSolver());

    private final Canvas canvas = new Canvas(COLS * CELL_SIZE, ROWS * CELL_SIZE);
    private final ComboBox<String> algorithmBox = new ComboBox<>();
    private Maze maze;
    private Timeline animation;

    @Override
    public void start(Stage stage) {
        algorithmBox.getItems().addAll("BFS", "DFS", "A*");
        algorithmBox.setValue("BFS");

        Button generateButton = new Button("迷路生成");
        generateButton.setOnAction(e -> generateMaze());

        Button solveButton = new Button("探索アニメーション開始");
        solveButton.setOnAction(e -> animateSolve());

        HBox controls = new HBox(10, algorithmBox, generateButton, solveButton);
        controls.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(controls);
        root.setCenter(canvas);

        stage.setTitle("迷路生成&探索ビジュアライザ");
        stage.setScene(new Scene(root));
        stage.show();

        generateMaze();
    }

    private void generateMaze() {
        if (animation != null) {
            animation.stop();
        }
        maze = generator.generate(COLS, ROWS, System.nanoTime());
        drawMaze();
    }

    /**
     * 選択中のアルゴリズムで経路を探索し、{@link Timeline}で経路上のマスを1つずつ塗りつぶしていく。
     */
    private void animateSolve() {
        if (maze == null) {
            return;
        }
        if (animation != null) {
            animation.stop();
        }
        MazeSolver solver = solvers.get(algorithmBox.getValue());
        List<Cell> path = solver.solve(maze);

        drawMaze();
        animation = new Timeline();
        for (int i = 0; i < path.size(); i++) {
            Cell cell = path.get(i);
            KeyFrame frame = new KeyFrame(Duration.millis(40.0 * i), e -> fillCell(cell, Color.CORNFLOWERBLUE));
            animation.getKeyFrames().add(frame);
        }
        animation.play();
    }

    private void drawMaze() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        for (int row = 0; row < maze.height(); row++) {
            for (int col = 0; col < maze.width(); col++) {
                Cell cell = new Cell(row, col);
                double x = col * CELL_SIZE;
                double y = row * CELL_SIZE;
                if (col + 1 < maze.width() && !maze.isConnected(cell, new Cell(row, col + 1))) {
                    gc.strokeLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE);
                }
                if (row + 1 < maze.height() && !maze.isConnected(cell, new Cell(row + 1, col))) {
                    gc.strokeLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
                }
            }
        }
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());

        fillCell(maze.start(), Color.LIGHTGREEN);
        fillCell(maze.goal(), Color.SALMON);
    }

    private void fillCell(Cell cell, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(cell.col() * CELL_SIZE + 2, cell.row() * CELL_SIZE + 2, CELL_SIZE - 4, CELL_SIZE - 4);
    }
}
