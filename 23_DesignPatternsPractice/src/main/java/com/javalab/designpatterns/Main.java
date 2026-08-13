package com.javalab.designpatterns;

import com.javalab.designpatterns.factory.Shape;
import com.javalab.designpatterns.factory.ShapeFactory;
import com.javalab.designpatterns.observer.WeatherStation;
import com.javalab.designpatterns.singleton.AppLogger;
import com.javalab.designpatterns.strategy.Checkout;
import com.javalab.designpatterns.strategy.CreditCardPayment;
import com.javalab.designpatterns.strategy.PayPalPayment;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * デザインパターン実践集のエントリーポイント。
 * Singleton({@link AppLogger})/Factory({@link ShapeFactory})/Observer({@link WeatherStation})/
 * Strategy({@link Checkout})の4パターンをコマンドで体験できる対話型REPLを提供する。
 */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    /**
     * REPLループ本体。テストから{@link Scanner}/{@link PrintStream}を差し替えられるよう分離している。
     * @param scanner コマンド読み取り元
     * @param out 結果出力先
     */
    static void run(Scanner scanner, PrintStream out) {
        ShapeFactory shapeFactory = new ShapeFactory();
        WeatherStation weatherStation = new WeatherStation();
        // Observerパターンの動作を目に見える形で示すため、通知内容を出力するObserverを1つ登録しておく。
        weatherStation.subscribe(temperature -> out.println("気温が変化しました: " + temperature + "℃"));
        Checkout checkout = new Checkout(new CreditCardPayment());

        out.println("デザインパターン実践集。コマンド: "
                + "log <メッセージ>(Singleton) / shape <circle|rectangle> <params...>(Factory) / "
                + "weather <温度>(Observer) / pay <creditcard|paypal> <金額>(Strategy) / exit");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            String command = parts[0];
            try {
                switch (command) {
                    case "exit" -> {
                        return;
                    }
                    case "log" -> handleLog(parts, out);
                    case "shape" -> handleShape(shapeFactory, parts, out);
                    case "weather" -> weatherStation.setTemperature(Double.parseDouble(parts[1]));
                    case "pay" -> handlePay(checkout, parts, out);
                    default -> out.println("不明なコマンドです: " + line);
                }
            } catch (RuntimeException e) {
                out.println("エラー: " + e.getMessage());
            }
        }
    }

    private static void handleLog(String[] parts, PrintStream out) {
        String message = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        AppLogger.getInstance().log(message);
        out.println("ログに記録しました: " + message);
    }

    private static void handleShape(ShapeFactory shapeFactory, String[] parts, PrintStream out) {
        double[] params = new double[parts.length - 2];
        for (int i = 2; i < parts.length; i++) {
            params[i - 2] = Double.parseDouble(parts[i]);
        }
        Shape shape = shapeFactory.create(parts[1], params);
        out.println("面積: " + shape.area());
    }

    private static void handlePay(Checkout checkout, String[] parts, PrintStream out) {
        checkout.setStrategy(switch (parts[1]) {
            case "creditcard" -> new CreditCardPayment();
            case "paypal" -> new PayPalPayment();
            default -> throw new IllegalArgumentException("未知の決済方法です: " + parts[1]);
        });
        out.println(checkout.checkout(Integer.parseInt(parts[2])));
    }
}
