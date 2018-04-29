/*
 * Copyright (c) 2018 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.liteweather.repository.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static com.journeyOS.liteweather.repository.db.Configs.WEATHER_CITY_ID;
import static com.journeyOS.liteweather.repository.db.Configs.WEATHER_DB_NAME;

@Dao
public interface WeatherDao {

    @Insert(onConflict = REPLACE)
    void saveWeather(Weather weather);

    @Query("DELETE FROM " + WEATHER_DB_NAME + " WHERE " + WEATHER_CITY_ID + " LIKE :" + WEATHER_CITY_ID)
    void deleteWeather(String cityId);

    @Query("SELECT * FROM " + WEATHER_DB_NAME + " WHERE " + WEATHER_CITY_ID + " LIKE :" + WEATHER_CITY_ID)
    Weather fetchWeather(String cityId);

    @Query("SELECT * FROM " + WEATHER_DB_NAME)
    List<Weather> fetchFollowedWeather();
}
