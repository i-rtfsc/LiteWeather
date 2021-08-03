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

package com.journeyOS.core.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.journeyOS.core.database.city.City;
import com.journeyOS.core.database.city.CityDao;
import com.journeyOS.core.database.global.Global;
import com.journeyOS.core.database.global.GlobalDao;
import com.journeyOS.core.database.weather.Weather;
import com.journeyOS.core.database.weather.WeatherDao;


@Database(entities = {Weather.class, City.class, Global.class},
        version = DBConfigs.DB_VERSION,
        exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDao weatherDao();

    public abstract CityDao cityDao();

    public abstract GlobalDao globalDao();
}
