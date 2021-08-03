package com.journeyOS.liteweather.data.source.local.city;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.journeyOS.liteweather.data.source.local.base.DBConfigs;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CityDao {
    @Query("SELECT * FROM " + DBConfigs.City.TABLE_NAME)
    List<City> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCities(List<City> cities);

    @Insert(onConflict = REPLACE)
    void insetOrUpdateCity(City city);

    @Query("SELECT * FROM " + DBConfigs.City.TABLE_NAME + " WHERE " + DBConfigs.City.CITY_NAME + " LIKE :cityName AND " + DBConfigs.City.ADM1 + " LIKE :adm1 LIMIT 1")
    City searchCity(String cityName, String adm1);

    @Query("SELECT * FROM " + DBConfigs.City.TABLE_NAME + " WHERE " + DBConfigs.City.LOCATION_ID + " LIKE :locationId  LIMIT 1")
    City searchCity(String locationId);

    @Query("SELECT * FROM " + DBConfigs.City.TABLE_NAME + " WHERE " + DBConfigs.City.LOCATION_ID + " LIKE  :key || '%' OR " + DBConfigs.City.ADM1 + " LIKE  :key || '%' OR " + DBConfigs.City.COUNTRY_EN + " LIKE  :key || '%' ")
    List<City> matchCity(String key);

}
