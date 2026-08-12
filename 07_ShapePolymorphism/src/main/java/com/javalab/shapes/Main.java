package com.javalab.shapes;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 対話式CLI図形計算ツールのエントリーポイント。
 * 標準入力からコマンド({@code circle}/{@code rectangle}/{@code triangle}/{@code list})を
 * 1行ずつ読み取り、結果を標準出力に表示する。{@code exit} で終了する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。{@link Scanner}/{@link PrintStream} を引数として受け取ることで、
     * テストから {@code StringReader}/{@code ByteArrayOutputStream} を注入できるようにしている。
     * @param scanner 入力読み取り元
     * @param out 出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        List<Shape> shapes = new ArrayList<>();

        out.println("図形計算ツールへようこそ。コマンド: circle <半径> / rectangle <幅> <高さ> / triangle <a> <b> <c> / list / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(shapes, line, out);
            } catch (NumberFormatException e) {
                // 不正な数値入力はクラッシュさせず、エラー表示して次の入力へ進む。
                out.println("エラー: 数値の形式が不正です");
            } catch (ShapeException | IllegalArgumentException e) {
                // 不正な寸法・不明なコマンドも同様に継続可能なエラーとして扱う。
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    /**
     * 1行分のコマンドを解釈して実行する。
     * @param shapes 登録済みの図形一覧
     * @param line 入力行
     * @param out 出力先
     * @throws ShapeException 図形の寸法が不正な場合
     * @throws NumberFormatException 数値が不正な場合
     * @throws IllegalArgumentException コマンドが不明、または引数の個数が不正な場合
     */
    private static void handleCommand(List<Shape> shapes, String line, PrintStream out) {
        if (line.equals("list")) {
            printShapes(shapes, out);
        } else if (line.startsWith("circle ")) {
            double radius = Double.parseDouble(line.substring(7).trim());
            shapes.add(new Circle(radius));
        } else if (line.startsWith("rectangle ")) {
            String[] parts = line.substring(10).trim().split("\\s+");
            if (parts.length != 2) {
                throw new IllegalArgumentException("入力形式が不正です: " + line);
            }
            shapes.add(new Rectangle(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
        } else if (line.startsWith("triangle ")) {
            String[] parts = line.substring(9).trim().split("\\s+");
            if (parts.length != 3) {
                throw new IllegalArgumentException("入力形式が不正です: " + line);
            }
            shapes.add(new Triangle(
                    Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2])));
        } else {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }

    private static void printShapes(List<Shape> shapes, PrintStream out) {
        double totalArea = 0;
        for (Shape shape : shapes) {
            out.println(shape.describe());
            totalArea += shape.area();
        }
        out.printf("合計面積=%.2f%n", totalArea);
    }
}
