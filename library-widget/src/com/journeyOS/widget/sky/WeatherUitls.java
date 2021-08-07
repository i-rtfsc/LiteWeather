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

package com.journeyOS.widget.sky;


import java.text.SimpleDateFormat;
import java.util.Date;


public class WeatherUitls {

    public static SkyType convertWeatherType(String weatherCode) {
        return convertWeatherType(weatherCode, false);
    }

    public static SkyType convertWeatherType(String weatherCode, boolean isNight) {
        try {
            final int w = Integer.valueOf(weatherCode);
            switch (w) {
                case 100:
                    return isNight ? SkyType.CLEAR_N : SkyType.CLEAR_D;
                case 101:// 多云
                case 102:// 少云
                case 103:// 晴间多云
                    return isNight ? SkyType.CLOUDY_N : SkyType.CLOUDY_D;
                case 104:// 阴
                    return isNight ? SkyType.OVERCAST_N : SkyType.OVERCAST_D;
                // 200 - 213是风
                case 200:
                case 201:
                case 202:
                case 203:
                case 204:
                case 205:
                case 206:
                case 207:
                case 208:
                case 209:
                case 210:
                case 211:
                case 212:
                case 213:
                    return isNight ? SkyType.WIND_N : SkyType.WIND_D;
                case 300:// 阵雨Shower Rain
                case 301:// 强阵雨 Heavy Shower Rain
                case 302:// 雷阵雨 Thundershower
                case 303:// 强雷阵雨 Heavy Thunderstorm
                case 304:// 雷阵雨伴有冰雹 Hail
                case 305:// 小雨 Light Rain
                case 306:// 中雨 Moderate Rain
                case 307:// 大雨 Heavy Rain
                case 308:// 极端降雨 Extreme Rain
                case 309:// 毛毛雨/细雨 Drizzle Rain
                case 310:// 暴雨 Storm
                case 311:// 大暴雨 Heavy Storm
                case 312:// 特大暴雨 Severe Storm
                case 313:// 冻雨 Freezing Rain
                    return isNight ? SkyType.RAIN_N : SkyType.RAIN_D;
                case 400:// 小雪 Light Snow
                case 401:// 中雪 Moderate Snow
                case 402:// 大雪 Heavy Snow
                case 403:// 暴雪 Snowstorm
                case 407:// 阵雪 Snow Flurry
                    return isNight ? SkyType.SNOW_N : SkyType.SNOW_D;
                case 404:// 雨夹雪 Sleet
                case 405:// 雨雪天气 Rain And Snow
                case 406:// 阵雨夹雪 Shower Snow
                    return isNight ? SkyType.RAIN_SNOW_N : SkyType.RAIN_SNOW_D;
                case 500:// 薄雾
                case 501:// 雾
                    return isNight ? SkyType.FOG_N : SkyType.FOG_D;
                case 502:// 霾
                case 504:// 浮尘
                    return isNight ? SkyType.HAZE_N : SkyType.HAZE_D;
                case 503:// 扬沙
                case 506:// 火山灰
                case 507:// 沙尘暴
                case 508:// 强沙尘暴
                    return isNight ? SkyType.SAND_N : SkyType.SAND_D;
                default:
                    return isNight ? SkyType.UNKNOWN_N : SkyType.UNKNOWN_D;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isNight ? SkyType.UNKNOWN_N : SkyType.UNKNOWN_D;
    }

    public static SkyType convertWeatherType(String weatherCode, String sunset, String sunrise) {
        final boolean isNight = isNight(sunset, sunrise);
        return convertWeatherType(weatherCode, isNight);
    }

    public static boolean isNight(String sunset, String sunrise) {
        //2021-02-20T06:58+08:00
        try {
            final Date date = new Date();

            if (sunset != null && sunrise != null) {
                final int curTime = Integer.valueOf((new SimpleDateFormat("HHmm").format(date)));
                final int srTime = Integer.valueOf(sunrise.replaceAll(":", ""));// 日出
                final int ssTime = Integer.valueOf(sunset.replaceAll(":", ""));// 日落
                if (curTime > srTime && curTime <= ssTime) {// 是白天
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
