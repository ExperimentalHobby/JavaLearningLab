package com.javalab.designpatterns;

import com.javalab.designpatterns.decorator.DiscountedPayment;
import com.javalab.designpatterns.factory.Shape;
import com.javalab.designpatterns.factory.ShapeFactory;
import com.javalab.designpatterns.observer.WeatherObserver;
import com.javalab.designpatterns.observer.WeatherStation;
import com.javalab.designpatterns.singleton.AppLogger;
import com.javalab.designpatterns.strategy.Checkout;
import com.javalab.designpatterns.strategy.CreditCardPayment;
import com.javalab.designpatterns.strategy.PayPalPayment;
import com.javalab.designpatterns.strategy.PaymentStrategy;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
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
        // 変数として保持しておくことで、CLIから"weather unsubscribe"を受け取った際に
        // unsubscribe()を呼び出せるようにする(unsubscribeをCLIから呼ぶ手段がなかった問題への対応)。
        WeatherObserver consoleObserver = temperature -> out.println("気温が変化しました: " + temperature + "℃");
        weatherStation.subscribe(consoleObserver);
        Checkout checkout = new Checkout(new CreditCardPayment());

        out.println("デザインパターン実践集。コマンド: "
                + "log <メッセージ>(Singleton) / logs(Singleton) / "
                + "shape <circle|rectangle> <params...>(Factory) / "
                + "weather <温度>(Observer) / weather unsubscribe(Observer) / "
                + "pay <creditcard|paypal> <金額> [割引率](Strategy/Decorator) / exit");
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
                    case "logs" -> handleLogs(out);
                    case "shape" -> handleShape(shapeFactory, parts, out);
                    case "weather" -> handleWeather(weatherStation, consoleObserver, parts);
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

    private static void handleWeather(WeatherStation weatherStation, WeatherObserver consoleObserver, String[] parts) {
        if (parts.length >= 2 && parts[1].equals("unsubscribe")) {
            weatherStation.unsubscribe(consoleObserver);
            return;
        }
        weatherStation.setTemperature(Double.parseDouble(parts[1]));
    }

    private static void handleLogs(PrintStream out) {
        List<String> logs = AppLogger.getInstance().logs();
        if (logs.isEmpty()) {
            out.println("ログはありません");
            return;
        }
        logs.forEach(out::println);
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
        PaymentStrategy strategy = switch (parts[1]) {
            case "creditcard" -> new CreditCardPayment();
            case "paypal" -> new PayPalPayment();
            default -> throw new IllegalArgumentException("未知の決済方法です: " + parts[1]);
        };
        // 第4引数(割引率)が指定された場合、Decoratorパターンで既存のPaymentStrategyをラップする。
        // Strategy(決済方法の丸ごと差し替え)とDecorator(既存実装への振る舞いの追加)の
        // 組み合わせを体験できる。
        if (parts.length >= 4) {
            strategy = new DiscountedPayment(strategy, Integer.parseInt(parts[3]));
        }
        checkout.setStrategy(strategy);
        out.println(checkout.checkout(Integer.parseInt(parts[2])));
    }
}
