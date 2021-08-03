package com.journeyOS.liteweather.data.source.local.base;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.journeyOS.liteweather.data.source.local.city.City;
import com.journeyOS.liteweather.data.source.local.city.CityDao;
import com.journeyOS.liteweather.data.source.local.weather.Weather;
import com.journeyOS.liteweather.data.source.local.weather.WeatherDao;


@Database(entities = {Weather.class, City.class},
        version = DBConfigs.DB_VERSION,
        exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDao weatherDao();

    public abstract CityDao cityDao();

}