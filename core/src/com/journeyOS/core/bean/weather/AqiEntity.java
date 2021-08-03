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

package com.journeyOS.core.bean.weather;

import java.util.List;

/**
 * https://www.heweather.com/documents/api/s6/weather
 */
public class AqiEntity {
    public List<HeWeather6Bean> HeWeather6;

    public static class HeWeather6Bean {
        public BasicBean basic;
        public UpdateBean update;
        public String status;
        public AirNowCityBean air_now_city;
        public List<AirNowStationBean> air_now_station;

        public static class BasicBean {
            public String cid;
            public String location;
            public String parent_city;
            public String admin_area;
            public String cnty;
            public String lat;
            public String lon;
            public String tz;
        }

        public static class UpdateBean {
            public String loc;
            public String utc;
        }

        public static class AirNowCityBean {
            public String aqi;
            public String qlty;
            public String main;
            public String pm25;
            public String pm10;
            public String no2;
            public String so2;
            public String co;
            public String o3;
            public String pub_time;
        }

        public static class AirNowStationBean {
            public String air_sta;
            public String aqi;
            public String asid;
            public String co;
            public String lat;
            public String lon;
            public String main;
            public String no2;
            public String o3;
            public String pm10;
            public String pm25;
            public String pub_time;
            public String qlty;
            public String so2;
        }
    }
}
