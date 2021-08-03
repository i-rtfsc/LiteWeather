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

import androidx.room.Entity;
import androidx.annotation.NonNull;

import static com.journeyOS.liteweather.repository.db.Configs.WEATHER_CITY_ID;
import static com.journeyOS.liteweather.repository.db.Configs.WEATHER_DB_NAME;

@Entity(tableName = WEATHER_DB_NAME, primaryKeys = {WEATHER_CITY_ID})
public class Weather {

    @NonNull
    public String cityId = "";
    public String weatherJson;

}
