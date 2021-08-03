package com.journeyOS.liteweather.data.source.local.base;

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
        //        {
        //            "location_id": "101010100",
        //            "location_name_en": "Beijing",
        //            "city_name": "北京",
        //            "country_code": "CN",
        //            "country_en": "China",
        //            "adm1_en": "Beijing",
        //            "adm1": "北京市",
        //            "adm2_en": "Beijing",
        //            "adm2": "北京",
        //            "latitude": "39.904987",
        //            "longitude": "116.405289"
        //        }
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

    }

    public static class Settings {
        public static final String CITY_INIT = "city_init";
        public static final boolean CITY_INIT_DEFAULT = false;

        public static final String LOCATION_ID = "current_location_id";
        public static final String LOCATION_ID_DEFAULT = "101020600";//上海浦东新区
    }
}
