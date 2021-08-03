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

public class DBConfigs {
    //database name
    public static final String DB_NAME = "core.db";
    //database version
    public static final int DB_VERSION = 1;

    public static class Weather {
        //table
        public static final String TABLE_NAME = "weather";
        //column
        public static final String CITY_ID = "cityId";
        //column
        public static final String DATA = "dataJson";
        //column
        public static final String TIME = "time";
    }

    public static class City {
        //table
        public static final String TABLE_NAME = "city";
        //column
        public static final String CITY_ID = "cityId";
        //column
        public static final String COUNTRY = "country";
        //column
        public static final String COUNTRY_EN = "countryEn";
        //column
        public static final String CITY_NAME = "cityName";
        //column
        public static final String PROVINCE = "province";
        //column
        public static final String PROVINCE_EN = "provinceEn";
        //column
        public static final String LONGITUDE = "longitude";
        //column
        public static final String LATITUDE = "latitude";
    }

    public static class Global {
        //table
        public static final String TABLE_NAME = "global";
        //column
        public static final String KEY = "key";
        //column
        public static final String VALUE = "value";
        //column
        public static final String OBJECT = "object";


        // key & value
        public static final String CITY_INIT = "city_init";
        public static final boolean CITY_INIT_DEFAULT = false;

        public static final String CURRENT_CITY = "current_city";
        public static final String ALL_CITIES = "all_cities";
    }


}
