package com.javalab.bmi;

/**
 * BMI(体格指数)の計算と、日本肥満学会基準に基づく判定を行う。
 */
public class BmiCalculator {

    /**
     * 身長・体重からBMIを計算する。
     * @param heightCm 身長(cm)
     * @param weightKg 体重(kg)
     * @return BMI(小数第2位で丸め)
     */
    public static double calculate(double heightCm, double weightKg) {
        double heightM = heightCm / 100.0;
        double bmi = weightKg / (heightM * heightM);
        return Math.round(bmi * 100) / 100.0;
    }

    /**
     * BMI値を日本肥満学会基準の4区分に判定する。
     * @param bmi 判定対象のBMI値
     * @return "低体重" / "普通体重" / "肥満(1度)" / "肥満(2度以上)"
     */
    public static String classify(double bmi) {
        if (bmi < 18.5) {
            return "低体重";
        }
        if (bmi < 25.0) {
            return "普通体重";
        }
        if (bmi < 30.0) {
            return "肥満(1度)";
        }
        return "肥満(2度以上)";
    }
}
