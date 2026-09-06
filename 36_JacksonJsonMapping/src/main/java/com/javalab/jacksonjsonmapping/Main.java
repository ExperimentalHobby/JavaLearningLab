package com.javalab.jacksonjsonmapping;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 商品カタログのJSON/XML相互変換ツールのエントリーポイント。
 * 標準入力からコマンド(add/list/toJson/toXml/fromJson/exit)を1行ずつ読み取り、結果を標準出力に表示する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner scanner, PrintStream out) {
        List<Product> products = new ArrayList<>();

        out.println("商品カタログJSON/XML変換ツールへようこそ。"
                + "コマンド: add <ID> <商品名> <価格> <発売日yyyy-MM-dd> / list / toJson / toXml / fromJson <JSON> / exit");

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                handleCommand(products, line, out);
            } catch (NumberFormatException | DateTimeParseException e) {
                out.println("エラー: 数値または日付の形式が不正です");
            } catch (IllegalArgumentException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleCommand(List<Product> products, String line, PrintStream out) {
        if (line.equals("list")) {
            products.forEach(p -> out.println(p));
        } else if (line.equals("toJson")) {
            out.println(ProductJsonMapper.toJson(products));
        } else if (line.equals("toXml")) {
            out.println(ProductXmlMapper.toXml(products));
        } else if (line.startsWith("add ")) {
            String[] parts = line.substring(4).trim().split("\\s+", 4);
            if (parts.length != 4) {
                throw new IllegalArgumentException("入力形式が不正です: " + line);
            }
            products.add(new Product(parts[0], parts[1], new BigDecimal(parts[2]), LocalDate.parse(parts[3])));
        } else if (line.startsWith("fromJson ")) {
            products.add(ProductJsonMapper.fromJson(line.substring(9).trim()));
        } else {
            throw new IllegalArgumentException("不明なコマンドです: " + line);
        }
    }
}
