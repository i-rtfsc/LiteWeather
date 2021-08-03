/*
 * Copyright (c) 2021 anqi.huang@outlook.com
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

package com.journeyOS.core.database.weather;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.journeyOS.core.database.DBConfigs;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WeatherDao {

    @Insert(onConflict = REPLACE)
    void saveWeather(Weather weather);

    @Query("SELECT * FROM " + DBConfigs.Weather.TABLE_NAME + " WHERE " + DBConfigs.Weather.CITY_ID + " LIKE :cityId")
    Weather getWeather(String cityId);

    @Query("DELETE FROM " + DBConfigs.Weather.TABLE_NAME + " WHERE " + DBConfigs.Weather.CITY_ID + " LIKE :cityId")
    void deleteWeather(String cityId);

    @Query("SELECT * FROM " + DBConfigs.Weather.TABLE_NAME)
    List<Weather> getAll();
}
