package com.javalab.designpatterns.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 気温変化を登録済みの{@link WeatherObserver}全員に通知するSubject(Observerパターンの観測対象役)。
 * WeatherStation自身は「誰が」気温を使って何をするかを一切知らず、
 * {@link WeatherObserver}インターフェースを実装した任意のオブジェクトを実行時に登録できる。
 * これにより通知先(表示用UI、ログ記録、アラート等)を自由に追加・削除でき、
 * WeatherStation側のコードを変更する必要がない(疎結合の実現)。
 */
public class WeatherStation {

    private final List<WeatherObserver> observers = new ArrayList<>();

    /**
     * 気温変化の通知を受け取るObserverを登録する。
     * @param observer 登録するObserver
     */
    public void subscribe(WeatherObserver observer) {
        observers.add(observer);
    }

    /**
     * 登録済みのObserverを解除する。以降、気温変化の通知を受け取らなくなる。
     * @param observer 解除するObserver
     */
    public void unsubscribe(WeatherObserver observer) {
        observers.remove(observer);
    }

    /**
     * 気温を更新し、登録済みの全Observerへ通知する。
     * @param temperature 新しい気温
     */
    public void setTemperature(double temperature) {
        // 登録済みの全Observerへ、登録順に同じ気温を通知する(いわゆる「ブロードキャスト」)。
        // WeatherStationは各Observerが通知を受けて何をするかには関与しない。
        for (WeatherObserver observer : observers) {
            observer.onTemperatureChanged(temperature);
        }
    }
}
