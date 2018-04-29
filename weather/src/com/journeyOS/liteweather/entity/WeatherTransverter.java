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

package com.journeyOS.liteweather.entity;


import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.api.weatherprovider.WeatherData;

import java.util.ArrayList;
import java.util.List;

public class WeatherTransverter {
    private static final String TAG = WeatherTransverter.class.getSimpleName();

    public static WeatherData convertFromHeWeather(HeWeather heWeather, AqiEntity heWeatherAqi) {
//        LogUtils.d(TAG, "convertFromHeWeather() called with: heWeather = [" + JsonHelper.toJson(heWeather) + "]");
//        LogUtils.d(TAG, "convertFromHeWeather() called with: heWeatherAqi = [" + JsonHelper.toJson(heWeatherAqi) + "]");
        WeatherData weatherData = new WeatherData();
        try {
            HeWeather.HeWeather6Bean heWeather6Bean = heWeather.getHeWeather6().get(0);
            HeWeather.HeWeather6Bean.BasicBean basicBean = heWeather6Bean.getBasic();
            HeWeather.HeWeather6Bean.NowBean nowBean = heWeather6Bean.getNow();

            List<HeWeather.HeWeather6Bean.HourlyBean> hourlyBeans = heWeather6Bean.getHourly();
            List<HeWeather.HeWeather6Bean.DailyForecastBean> dailyForecastBeans = heWeather6Bean.getDailyForecast();
            List<HeWeather.HeWeather6Bean.LifestyleBean> lifestyleBeans = heWeather6Bean.getLifestyle();

            weatherData.cityId = basicBean.getCid();

            WeatherData.BasicEntity basicEntity = new WeatherData.BasicEntity();
            basicEntity.city = basicBean.getLocation();
            weatherData.basic = basicEntity;

            if (!BaseUtils.isNull(hourlyBeans)) {
                List<WeatherData.HoursEntity> hours = new ArrayList<>();
                for (HeWeather.HeWeather6Bean.HourlyBean hourlyBean : hourlyBeans) {
                    WeatherData.HoursEntity hoursEntity = new WeatherData.HoursEntity();
                    hoursEntity.weather = hourlyBean.getCond_txt();
                    hoursEntity.weatherCode = hourlyBean.getCond_code();
                    hoursEntity.windDirection = hourlyBean.getWind_dir();
                    hoursEntity.windLevel = hourlyBean.getWind_sc();
                    hoursEntity.windSpeed = hourlyBean.getWind_spd();
                    hoursEntity.temperature = hourlyBean.getTmp();
                    hoursEntity.time = hourlyBean.getTime();
                    hoursEntity.humidity = hourlyBean.getHum();
                    hoursEntity.pop = hourlyBean.getPop();
                    hours.add(hoursEntity);
                }
                weatherData.hours = hours;
            }

            if (!BaseUtils.isNull(dailyForecastBeans)) {
                List<WeatherData.DailyEntity> dailyEntities = new ArrayList<>();
                for (HeWeather.HeWeather6Bean.DailyForecastBean dailyForecastBean : dailyForecastBeans) {
                    WeatherData.DailyEntity dailyEntity = new WeatherData.DailyEntity();
                    dailyEntity.date = dailyForecastBean.getDate();
                    dailyEntity.sunRise = dailyForecastBean.getSr();
                    dailyEntity.sunSet = dailyForecastBean.getSs();
                    dailyEntity.TMax = dailyForecastBean.getTmp_max();
                    dailyEntity.TMin = dailyForecastBean.getTmp_min();
                    dailyEntity.weatherDay = dailyForecastBean.getCond_txt_d();
                    dailyEntity.weatherNight = dailyForecastBean.getCond_txt_n();
                    dailyEntity.pop = dailyForecastBean.getPop();
                    dailyEntities.add(dailyEntity);
                }
                weatherData.daily = dailyEntities;
            }

            if (!BaseUtils.isNull(lifestyleBeans)) {
                List<WeatherData.LifeIndexEntity> lifeIndexEntities = new ArrayList<>();
                for (HeWeather.HeWeather6Bean.LifestyleBean lifestyleBean : lifestyleBeans) {
                    WeatherData.LifeIndexEntity lifeIndexEntity = new WeatherData.LifeIndexEntity();
                    lifeIndexEntity.name = lifestyleBean.getType();
                    lifeIndexEntity.level = lifestyleBean.getBrf();
                    lifeIndexEntity.content = lifestyleBean.getTxt();
                    lifeIndexEntities.add(lifeIndexEntity);
                }
                weatherData.life = lifeIndexEntities;
            }

            if (!BaseUtils.isNull(heWeatherAqi) && !BaseUtils.isNull(heWeatherAqi.HeWeather6)) {
                AqiEntity.HeWeather6Bean.AirNowCityBean airNowCityBean = heWeatherAqi.HeWeather6.get(0).air_now_city;
                WeatherData.AqiEntity aqiEntity = new WeatherData.AqiEntity();
                aqiEntity.aqi = airNowCityBean.aqi;
                aqiEntity.pm25 = airNowCityBean.pm25;
                aqiEntity.pm10 = airNowCityBean.pm10;
                aqiEntity.quality = airNowCityBean.qlty;
                aqiEntity.so2 = airNowCityBean.so2;
                aqiEntity.no2 = airNowCityBean.no2;
                weatherData.aqi = aqiEntity;
            }

            if (!BaseUtils.isNull(nowBean)) {
                WeatherData.NowEntity nowEntity = new WeatherData.NowEntity();
                nowEntity.bodyT = nowBean.getFl();
                nowEntity.visibility = nowBean.getVis();
                nowEntity.humidity = nowBean.getHum();
                nowEntity.precipitation = nowBean.getPcpn();
                nowEntity.weather = nowBean.getCond_txt();
                nowEntity.weatherCode = nowBean.getCond_code();
                nowEntity.temperature = nowBean.getTmp();
                nowEntity.windDirection = nowBean.getWind_dir();
                nowEntity.windLevel = nowBean.getWind_sc();
                nowEntity.windSpeed = nowBean.getWind_spd();
                nowEntity.pressure = nowBean.getPres();
                weatherData.now = nowEntity;
            }

        } catch (Exception e) {
            LogUtils.e(TAG, "convert HeWeather error = " + e);
        }

        return weatherData;
    }

}
