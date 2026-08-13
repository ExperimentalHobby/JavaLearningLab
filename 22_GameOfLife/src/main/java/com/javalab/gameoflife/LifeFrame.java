package com.javalab.gameoflife;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

/**
 * ライフゲームのメインウィンドウ。
 * 中央にグリッドを描画する{@link JPanel}、下部に操作ボタンを{@link BorderLayout}で配置する。
 * GUIの描画・イベント配線自体は自動テスト対象外のため、実際にアプリを起動して手動確認している。
 */
public class LifeFrame extends JFrame {

    private static final int ROWS = 25;
    private static final int COLS = 40;
    private static final int CELL_SIZE = 16;
    private static final int TIMER_DELAY_MS = 150;

    private final GameOfLife gameOfLife = new GameOfLife();
    private final Random random = new Random();
    private final JPanel canvas;
    private final Timer timer;
    private final JLabel generationLabel = new JLabel("世代: 0");

    private Grid grid = new Grid(COLS, ROWS);
    private int generation = 0;

    public LifeFrame() {
        super("ライフゲーム");

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
            }
        };
        canvas.setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onCellClicked(e.getX(), e.getY());
            }
        });

        // タイマー駆動更新: 一定間隔ごとにstep()を呼び、世代を進めて再描画する。
        timer = new Timer(TIMER_DELAY_MS, e -> step());

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        add(buildControlPanel(), BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildControlPanel() {
        JButton startButton = new JButton("開始");
        startButton.addActionListener(e -> timer.start());

        JButton stopButton = new JButton("停止");
        stopButton.addActionListener(e -> timer.stop());

        JButton stepButton = new JButton("1世代進める");
        stepButton.addActionListener(e -> step());

        JButton clearButton = new JButton("クリア");
        clearButton.addActionListener(e -> {
            timer.stop();
            grid = new Grid(COLS, ROWS);
            resetGeneration();
        });

        JButton randomButton = new JButton("ランダム生成");
        randomButton.addActionListener(e -> {
            timer.stop();
            grid = randomGrid();
            resetGeneration();
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(startButton);
        panel.add(stopButton);
        panel.add(stepButton);
        panel.add(clearButton);
        panel.add(randomButton);
        panel.add(generationLabel);
        return panel;
    }

    private void onCellClicked(int x, int y) {
        int col = x / CELL_SIZE;
        int row = y / CELL_SIZE;
        if (row >= 0 && row < grid.height() && col >= 0 && col < grid.width()) {
            grid.setAlive(row, col, !grid.isAlive(row, col));
            canvas.repaint();
        }
    }

    private void step() {
        grid = gameOfLife.nextGeneration(grid);
        generation++;
        updateGenerationLabel();
        canvas.repaint();
    }

    private void resetGeneration() {
        generation = 0;
        updateGenerationLabel();
        canvas.repaint();
    }

    private Grid randomGrid() {
        Grid newGrid = new Grid(COLS, ROWS);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                // 2回のnextBooleanのANDで生存率を約25%に抑え、初期盤面が密集しすぎないようにする。
                newGrid.setAlive(row, col, random.nextBoolean() && random.nextBoolean());
            }
        }
        return newGrid;
    }

    private void updateGenerationLabel() {
        generationLabel.setText("世代: " + generation);
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);
        for (int row = 0; row < grid.height(); row++) {
            for (int col = 0; col < grid.width(); col++) {
                if (grid.isAlive(row, col)) {
                    g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        g.setColor(Color.LIGHT_GRAY);
        for (int row = 0; row <= grid.height(); row++) {
            g.drawLine(0, row * CELL_SIZE, grid.width() * CELL_SIZE, row * CELL_SIZE);
        }
        for (int col = 0; col <= grid.width(); col++) {
            g.drawLine(col * CELL_SIZE, 0, col * CELL_SIZE, grid.height() * CELL_SIZE);
        }
    }
}
