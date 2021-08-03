package com.journeyOS.liteweather.data.source;

import androidx.annotation.NonNull;

import com.journeyOS.liteweather.data.source.local.city.City;
import com.journeyOS.liteweather.data.source.local.weather.Weather;

import java.util.List;

public interface LocalDataSource {
    void put(@NonNull String key, @NonNull Object defaultValue);

    String getString(@NonNull String key, @NonNull String defaultValue);

    int getInt(@NonNull String key, int defaultValue);

    boolean getBoolean(@NonNull String key, boolean defaultValue);

    float getFloat(@NonNull String key, float defaultValue);

    City searchCity(String cityName, String adm1);

    City searchCity(String locationId);

    List<City> matchCity(String key);

    void saveWeather(Weather weather);

    Weather getWeather(String locationId);

    void deleteWeather(String locationId);

    List<Weather> getAllWeather();
}
