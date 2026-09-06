package com.javalab.algorithmsdatastructures;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * ソートアルゴリズム比較・二分探索木のエントリーポイント。
 * 標準入力からコマンド(sort/bst/exit)を1行ずつ読み取り、結果を標準出力に表示する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner scanner, PrintStream out) {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();

        out.println("アルゴリズム/データ構造ツールへようこそ。"
                + "コマンド: sort <selection|insertion|quick|merge> <値...> / "
                + "bst insert <値> / bst contains <値> / bst inorder / bst size / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(tree, line, out);
            } catch (NumberFormatException e) {
                out.println("エラー: 数値の形式が不正です");
            } catch (IllegalArgumentException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleCommand(BinarySearchTree<Integer> tree, String line, PrintStream out) {
        if (line.startsWith("sort ")) {
            handleSort(line.substring(5).trim(), line, out);
        } else if (line.startsWith("bst ")) {
            handleBst(tree, line.substring(4).trim(), line, out);
        } else {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }

    private static void handleSort(String rest, String line, PrintStream out) {
        String[] parts = rest.split("\\s+", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("入力形式が不正です: " + line);
        }
        List<Integer> values = parseInts(parts[1]);

        List<Integer> sorted = switch (parts[0]) {
            case "selection" -> SortAlgorithms.selectionSort(values);
            case "insertion" -> SortAlgorithms.insertionSort(values);
            case "quick" -> SortAlgorithms.quickSort(values);
            case "merge" -> SortAlgorithms.mergeSort(values);
            default -> throw new IllegalArgumentException("不明なアルゴリズムです: " + parts[0]);
        };

        out.println(sorted.stream().map(String::valueOf).collect(Collectors.joining(" ")));
    }

    private static void handleBst(BinarySearchTree<Integer> tree, String rest, String line, PrintStream out) {
        String[] parts = rest.split("\\s+", 2);
        switch (parts[0]) {
            case "insert" -> {
                requireArg(parts, line);
                tree.insert(Integer.parseInt(parts[1].trim()));
                out.println("挿入しました: " + parts[1].trim());
            }
            case "contains" -> {
                requireArg(parts, line);
                out.println(tree.contains(Integer.parseInt(parts[1].trim())));
            }
            case "inorder" -> out.println(
                    tree.inOrderTraversal().stream().map(String::valueOf).collect(Collectors.joining(" ")));
            case "size" -> out.println(tree.size());
            default -> throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }

    private static void requireArg(String[] parts, String line) {
        if (parts.length != 2 || parts[1].isBlank()) {
            throw new IllegalArgumentException("入力形式が不正です: " + line);
        }
    }

    private static List<Integer> parseInts(String text) {
        return Arrays.stream(text.trim().split("\\s+")).map(Integer::parseInt).toList();
    }
}
