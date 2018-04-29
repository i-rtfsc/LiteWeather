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

package com.journeyOS.core.base;

import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.core.R;

import java.util.HashMap;
import java.util.Map;

public class ResourceProvider {

    private ResourceProvider() {
    }

    private static Map<String, Integer> sWeatherIcons = new HashMap<>();

    private static final int[] NOTIFICATION_THEMES = {R.layout.notification_system, R.layout.notification_white};

    private static final long[] SCHEDULES = {30 * 60, 60 * 60, 3 * 60 * 60, 0};
    private static final String[] SUNNY = {"晴", "多云"};
    private static final String[] WEATHERS = {
            "阴",
            "晴",
            "多云",
            "大雨",
            "雨",
            "雪",
            "风",
            "雾霾",
            "雨夹雪"};
    private static final int[] ICONS_ID = {
            R.drawable.svg_weather_clouds,
            R.drawable.svg_weather_sunny,
            R.drawable.svg_weather_few_clouds,
            R.drawable.svg_weather_big_rain,
            R.drawable.svg_weather_rain,
            R.drawable.svg_weather_snow,
            R.drawable.svg_weather_wind,
            R.drawable.svg_weather_haze,
            R.drawable.svg_weather_rain_snow};

    static {
        for (int index = 0; index < WEATHERS.length; index++) {
            sWeatherIcons.put(WEATHERS[index], ICONS_ID[index]);
        }
    }

    public static long getSchedule(int which) {
        return SCHEDULES[which];
    }

    public static int getNotificationThemeId(int which) {
        return NOTIFICATION_THEMES[which];
    }


    public static boolean sunny(String weather) {
        for (String weatherKey : SUNNY) {
            if (weatherKey.contains(weather) || weather.contains(weatherKey)) {
                return true;
            }
        }
        return false;
    }

    public static int getIconId(String weather) {
        if (BaseUtils.isEmpty(weather)) {
            return R.drawable.svg_weather_unknow;
        }

        if (sWeatherIcons.get(weather) != null) {
            return sWeatherIcons.get(weather);
        }

        for (String weatherKey : sWeatherIcons.keySet()) {
            if (weatherKey.contains(weather) || weather.contains(weatherKey)) {
                return sWeatherIcons.get(weatherKey);
            }
        }

        return R.drawable.svg_weather_unknow;
    }

}
