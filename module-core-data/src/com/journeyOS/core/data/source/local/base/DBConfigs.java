package com.journeyOS.core.data.source.local.base;

import java.util.ArrayList;
import java.util.List;

public class DBConfigs {
    //database name
    public static final String DB_NAME = "data.db";
    //database version
    public static final int DB_VERSION = 1;

    public static class Weather {
        //table
        public static final String TABLE_NAME = "weather";
        //column
        public static final String LOCATION_ID = "location_id";
        //column
        public static final String ADM1 = "adm1";
        //column
        public static final String CITY_NAME = "city_name";
        //column
        public static final String AIR = "air";
        //column
        public static final String INDICES = "indices";
        //column
        public static final String NOW = "now";
        //column
        public static final String SUN = "sun";
        //column
        public static final String DAILY = "daily";
        //column
        public static final String HOURLY = "hourly";
        //column
        public static final String TIME = "time";
    }

    //https://github.com/qwd/LocationList
    public static class City {
        //table
        public static final String TABLE_NAME = "city";

        //column
        public static final String LOCATION_ID = "location_id";
        //column
        public static final String LOCATION_NAME_EN = "location_name_en";
        //column
        public static final String CITY_NAME = "city_name";
        //column
        public static final String COUNTRY_CODE = "country_code";
        //column
        public static final String COUNTRY_EN = "country_en";
        //column
        public static final String ADM1_EN = "adm1_en";
        //column
        public static final String ADM1 = "adm1";
        //column
        public static final String ADM2_EN = "adm2_en";
        //column
        public static final String ADM2 = "adm2";
        //column
        public static final String LATITUDE = "latitude";
        //column
        public static final String LONGITUDE = "longitude";


        public static com.journeyOS.core.data.source.local.city.City getDefaultCity() {
            com.journeyOS.core.data.source.local.city.City city = new com.journeyOS.core.data.source.local.city.City();
            city.locationId = "101020600";
            city.locationNameEn = "Pudongxinqu";
            city.cityName = "浦东新区";
            city.countryCode = "CN";
            city.countryEn = "China";
            city.adm1En = "Shanghai";
            city.adm1 = "上海市";
            city.adm2En = "Shanghai";
            city.adm2 = "上海";
            city.longitude = "121.567703";
            city.latitude = "31.245943";

            return city;
        }
    }

    public static class Settings {
        public static final String CITY_INIT = "city_init";
        public static final boolean CITY_INIT_DEFAULT = false;

        public static final String LOCATION_ID = "current_location_id";
        public static final String LOCATION_ID_DEFAULT = "101020600";//上海浦东新区

        public static final String WEATHER_KEY = "key";
        public static final String WEATHER_KEY_DEFAULT = "8aeec77017724b518a5f0ba5d1888820";

        public static final String NIGHT_SKY = "night_sky";
        public static final boolean NIGHT_SKY_DEFAULT = false;

        public static List<String> getWeatherKeys() {
            List<String> keys = new ArrayList<>();
            keys.add("8aeec77017724b518a5f0ba5d1888820");
            keys.add("7e0c26e74f384de59efb7a86565a1c0f");
            keys.add("def9a507328e4cd395d983fe2589586e");
            keys.add("537664b7e2124b3c845bc0b51278d4af");
            keys.add("bc0418b57b2d4918819d3974ac1285d9");

            return keys;
        }

    }
}
