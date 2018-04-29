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

package com.journeyOS.core.api.weatherprovider;

import java.util.List;

/**
 * 如果更换天气接口，将新的天气格式转换成WeatherData
 * https://www.heweather.com/documents/api/s6/weather
 * https://www.heweather.com/documents/api/s6/air-now
 */
public class WeatherData {
    public String cityId;
    public BasicEntity basic;
    public AqiEntity aqi;
    public List<HoursEntity> hours;
    public List<DailyEntity> daily;
    public List<LifeIndexEntity> life;
    public NowEntity now;

    public static class BasicEntity {
        public String city;
    }

    public static class AqiEntity {
        public String aqi;
        public String pm10;
        public String pm25;
        public String so2;
        public String no2;
        public String quality;
    }

    public static class HoursEntity {
        //天气状况代码（cond_code）
        public String weatherCode;
        //天气状况（cond_txt）
        public String weather;
        //风向（wind_dir）
        public String windDirection;
        //风力（wind_sc）
        public String windLevel;
        //风速（wind_spd）
        public String windSpeed;
        //温度
        public String temperature;
        public String time;
        //相对湿度（hum）
        public String humidity;
        //probability	of	precipitation
        public String pop;
    }

    public static class DailyEntity {
        public String date;
        //日出时间（sr）
        public String sunRise;
        //日落时间（ss）
        public String sunSet;
        //最高温度 (tmp_max)
        public String TMax;
        //最低温度 (tmp_min)
        public String TMin;
        //白天天气状况描述
        public String weatherDay;
        //晚间天气状况描述
        public String weatherNight;
        //probability of precipitation
        public String pop;
    }

    public static class NowEntity {
        //体感温度(fl)
        public String bodyT;
        //相对湿度（hum）
        public String humidity;
        //降水量（pcpn）
        public String precipitation;
        //温度
        public String temperature;
        //能见度
        public String visibility;
        //天气状况（cond_txt）
        public String weather;
        //天气状况代码（cond_code）
        public String weatherCode;
        //风向（wind_dir）
        public String windDirection;
        //风力（wind_sc）
        public String windLevel;
        //风速（wind_spd）
        public String windSpeed;
        //大气压强
        public String pressure;
    }

    public static class LifeIndexEntity {
        public String name;
        public String level;
        public String content;
    }
}
