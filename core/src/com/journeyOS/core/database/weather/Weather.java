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

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.journeyOS.core.database.DBConfigs;

@Entity(tableName = DBConfigs.Weather.TABLE_NAME, primaryKeys = {DBConfigs.Weather.CITY_ID})
public class Weather {

    @NonNull
    @ColumnInfo(name = DBConfigs.Weather.CITY_ID)
    public String cityId = "";

    @ColumnInfo(name = DBConfigs.Weather.DATA)
    public String data;

    @ColumnInfo(name = DBConfigs.Weather.TIME)
    public String time;

}
