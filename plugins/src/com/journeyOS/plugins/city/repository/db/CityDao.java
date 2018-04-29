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

package com.journeyOS.plugins.city.repository.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.journeyOS.core.api.cityprovider.City;

import java.util.List;


@Dao
public interface CityDao {
    @Query("SELECT * FROM city ")
    List<City> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCities(List<City> cities);

    @Query("SELECT * FROM city WHERE cityName LIKE :cityName AND country LIKE :country LIMIT 1")
    City searchCity(String cityName, String country);

    @Query("SELECT * FROM city WHERE cityId LIKE :cityId  LIMIT 1")
    City searchCity(String cityId);

    @Query("SELECT * FROM city WHERE cityId LIKE  :key || '%' OR country LIKE  :key || '%' OR countryEn LIKE  :key || '%' ")
    List<City> matchCity(String key);

}
