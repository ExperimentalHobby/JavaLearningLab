package com.javalab.designpatterns.observer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link WeatherStation} のObserverパターン実装(登録・解除・ブロードキャスト通知)を検証するテスト。
 * {@link WeatherObserver}はラムダ式(メソッド参照 {@code received::add})で実装しており、
 * 匿名クラスを書かずに手軽なObserverを用意できることも示している。
 */
class WeatherStationTest {

    private final WeatherStation station = new WeatherStation();

    @Test
    void setTemperatureNotifiesAllSubscribedObservers() {
        // 2つのObserverを登録し、setTemperature()の呼び出し1回で両方に同じ気温が
        // 通知される(ブロードキャストされる)ことを確認する。
        List<Double> received1 = new ArrayList<>();
        List<Double> received2 = new ArrayList<>();
        station.subscribe(received1::add);
        station.subscribe(received2::add);

        station.setTemperature(25.0);

        assertEquals(List.of(25.0), received1);
        assertEquals(List.of(25.0), received2);
    }

    @Test
    void unsubscribedObserverDoesNotReceiveNotifications() {
        // unsubscribe()で登録解除したObserverには、以降の通知が一切届かないことを確認する。
        List<Double> received = new ArrayList<>();
        WeatherObserver observer = received::add;
        station.subscribe(observer);
        station.unsubscribe(observer);

        station.setTemperature(25.0);

        assertEquals(List.of(), received);
    }
}
