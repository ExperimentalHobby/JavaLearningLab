package com.javalab.designpatterns.observer;

/**
 * 気温変化の通知を受け取るリスナー。Observerパターンのミニアプリ実装。
 */
public interface WeatherObserver {

    void onTemperatureChanged(double temperature);
}
