package com.javalab.designpatterns.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 気温変化を登録済みの{@link WeatherObserver}全員に通知するSubject。
 */
public class WeatherStation {

    private final List<WeatherObserver> observers = new ArrayList<>();

    public void subscribe(WeatherObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(WeatherObserver observer) {
        observers.remove(observer);
    }

    /**
     * 気温を更新し、登録済みの全Observerへ通知する。
     * @param temperature 新しい気温
     */
    public void setTemperature(double temperature) {
        for (WeatherObserver observer : observers) {
            observer.onTemperatureChanged(temperature);
        }
    }
}
