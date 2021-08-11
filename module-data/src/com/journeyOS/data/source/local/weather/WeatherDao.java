package com.journeyOS.data.source.local.weather;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.journeyOS.data.source.local.base.DBConfigs;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WeatherDao {

    @Insert(onConflict = REPLACE)
    void saveWeather(Weather weather);

    @Query("SELECT * FROM " + DBConfigs.Weather.TABLE_NAME + " WHERE " + DBConfigs.Weather.LOCATION_ID + " LIKE :locationId")
    Weather getWeather(String locationId);

    @Query("DELETE FROM " + DBConfigs.Weather.TABLE_NAME + " WHERE " + DBConfigs.Weather.LOCATION_ID + " LIKE :locationId")
    void deleteWeather(String locationId);

    @Query("SELECT * FROM " + DBConfigs.Weather.TABLE_NAME)
    List<Weather> getAll();
}
