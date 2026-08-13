package com.javalab.designpatterns.observer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeatherStationTest {

    private final WeatherStation station = new WeatherStation();

    @Test
    void setTemperatureNotifiesAllSubscribedObservers() {
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
        List<Double> received = new ArrayList<>();
        WeatherObserver observer = received::add;
        station.subscribe(observer);
        station.unsubscribe(observer);

        station.setTemperature(25.0);

        assertEquals(List.of(), received);
    }
}
