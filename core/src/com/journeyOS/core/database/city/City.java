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

package com.journeyOS.core.database.city;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.journeyOS.core.database.DBConfigs;


@Entity(tableName = DBConfigs.City.TABLE_NAME, primaryKeys = {DBConfigs.City.CITY_ID})
public class City {

    @NonNull
    @ColumnInfo(name = DBConfigs.City.CITY_ID)
    public String cityId = "";

    @ColumnInfo(name = DBConfigs.City.COUNTRY)
    public String country;

    @ColumnInfo(name = DBConfigs.City.COUNTRY_EN)
    public String countryEn;

    @ColumnInfo(name = DBConfigs.City.CITY_NAME)
    public String cityName;

    @ColumnInfo(name = DBConfigs.City.PROVINCE)
    public String province;

    @ColumnInfo(name = DBConfigs.City.PROVINCE_EN)
    public String provinceEn;

    @ColumnInfo(name = DBConfigs.City.LONGITUDE)
    public String longitude;

    @ColumnInfo(name = DBConfigs.City.LATITUDE)
    public String latitude;

    @Ignore
    @Override
    public String toString() {
        return "City{" +
                "cityId='" + cityId + '\'' +
                ", country='" + country + '\'' +
                ", countryEn='" + countryEn + '\'' +
                ", cityName='" + cityName + '\'' +
                ", province='" + province + '\'' +
                ", provinceEn='" + provinceEn + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
