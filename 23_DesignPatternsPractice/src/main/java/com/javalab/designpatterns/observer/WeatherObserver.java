package com.javalab.designpatterns.observer;

/**
 * 気温変化の通知を受け取るリスナー(Observerパターンの観測者役)。
 * {@link WeatherStation}へ{@link WeatherStation#subscribe(WeatherObserver)}で登録すると、
 * 気温が更新されるたびにこのメソッドが呼び出される。
 */
public interface WeatherObserver {

    /**
     * 気温が変化したときに呼び出される。
     * @param temperature 新しい気温
     */
    void onTemperatureChanged(double temperature);
}
