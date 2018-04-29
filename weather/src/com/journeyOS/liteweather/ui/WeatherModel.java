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

package com.journeyOS.liteweather.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.cityprovider.ICityProvider;
import com.journeyOS.core.api.weatherprovider.WeatherData;
import com.journeyOS.core.base.StatusDataResource;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.literouter.Message;
import com.journeyOS.literouter.RouterListener;
import com.journeyOS.liteweather.R;
import com.journeyOS.liteweather.api.IFetchWeather;
import com.journeyOS.liteweather.repository.WeatherRepository;
import com.journeyOS.liteweather.view.AqiView;
import com.journeyOS.liteweather.view.AstroView;
import com.journeyOS.liteweather.view.DailyForecastView;
import com.journeyOS.liteweather.view.HourlyForecastView;
import com.journeyOS.plugins.city.ui.search.SearchActivity;

import java.util.Arrays;
import java.util.List;

public class WeatherModel extends BaseViewModel implements RouterListener {
    private static final String TAG = WeatherModel.class.getSimpleName();

    private MutableLiveData<StatusDataResource.Status> mGetWeatherStatus;

    private WeatherData mWeatherData;

    public WeatherData getWeatherData() {
        return mWeatherData;
    }


    @Override
    protected void onCreate() {
        mGetWeatherStatus = new MutableLiveData<>();
        WeatherRepository.getInstance().getWeatherObserver().observe(this, new Observer<StatusDataResource<WeatherData>>() {
            @Override
            public void onChanged(@Nullable StatusDataResource<WeatherData> statusDataResource) {
                try {
                    parseWeather(statusDataResource.data);
                } catch (Exception e) {
                    LogUtils.e(TAG, "parse weather date error = " + e);
                }

                mGetWeatherStatus.setValue(statusDataResource.status);
            }

        });
    }

    LiveData<StatusDataResource.Status> getGetWeatherStatus() {
        return mGetWeatherStatus;
    }


    void updateWeather(String cityId) {
        CoreManager.getImpl(IFetchWeather.class).queryWeather(cityId);
    }

    private void parseWeather(WeatherData weatherData) {
        mWeatherData = weatherData;
    }

    protected List<DailyForecastView.DailyData> parseDailyData(WeatherData weatherData) {
        List<WeatherData.DailyEntity> dailyEntityList = weatherData.daily;
        if (BaseUtils.isNull(dailyEntityList)) {
            return null;
        }
        int all_max = Integer.MIN_VALUE;
        int all_min = Integer.MAX_VALUE;

        DailyForecastView.DailyData[] dailyDataList = new DailyForecastView.DailyData[dailyEntityList.size()];
        for (int i = 0; i < dailyEntityList.size(); i++) {
            DailyForecastView.DailyData data = new DailyForecastView.DailyData();
            WeatherData.DailyEntity dailyEntity = dailyEntityList.get(i);

            int max = Integer.valueOf(dailyEntity.TMax);
            int min = Integer.valueOf(dailyEntity.TMin);
            if (all_max < max) {
                all_max = max;
            }
            if (all_min > min) {
                all_min = min;
            }

            data.Tmax = max;
            data.Tmin = min;
            data.date = dailyEntity.date;
            data.pop = dailyEntity.pop;
            data.weather = dailyEntity.weatherDay;

            dailyDataList[i] = data;
        }

        float all_distance = Math.abs(all_max - all_min);
        float average_distance = (all_max + all_min) / 2f;
        for (DailyForecastView.DailyData d : dailyDataList) {
            d.maxOffsetPercent = (d.Tmax - average_distance) / all_distance;
            d.minOffsetPercent = (d.Tmin - average_distance) / all_distance;
        }

        return Arrays.asList(dailyDataList);
    }

    protected List<HourlyForecastView.HourlyData> parseHourlyData(WeatherData weatherData) {
        List<WeatherData.HoursEntity> hoursEntityList = weatherData.hours;
        if (BaseUtils.isNull(hoursEntityList)) {
            return null;
        }

        HourlyForecastView.HourlyData[] hourlyDataList = new HourlyForecastView.HourlyData[hoursEntityList.size()];
        int all_max = Integer.MIN_VALUE;
        int all_min = Integer.MAX_VALUE;
        for (int i = 0; i < hoursEntityList.size(); i++) {
            WeatherData.HoursEntity hoursEntity = hoursEntityList.get(i);
            HourlyForecastView.HourlyData hourlyData = new HourlyForecastView.HourlyData();
            int tmp = Integer.valueOf(hoursEntity.temperature);
            if (all_max < tmp) {
                all_max = tmp;
            }
            if (all_min > tmp) {
                all_min = tmp;
            }
            hourlyData.temperature = tmp;
            hourlyData.date = hoursEntity.time;
            hourlyData.windLevel = hoursEntity.windLevel;
            hourlyData.humidity = hoursEntity.humidity;
            hourlyDataList[i] = hourlyData;
        }
        float all_distance = Math.abs(all_max - all_min);
        float average_distance = (all_max + all_min) / 2f;

        for (HourlyForecastView.HourlyData d : hourlyDataList) {
            d.offsetPercent = (d.temperature - average_distance) / all_distance;
        }

        return Arrays.asList(hourlyDataList);
    }

    protected AqiView.AqiData parseAqiData(WeatherData weatherData) {
        if (BaseUtils.isNull(weatherData)) {
            return null;
        }

        AqiView.AqiData aqiData = new AqiView.AqiData();
        aqiData.aqi = weatherData.aqi.aqi;
        aqiData.pm25 = weatherData.aqi.pm25;
        aqiData.pm10 = weatherData.aqi.pm10;
        aqiData.so2 = weatherData.aqi.so2;
        aqiData.no2 = weatherData.aqi.no2;
        aqiData.quality = weatherData.aqi.quality;

        return aqiData;
    }

    protected AstroView.AstroData parseAstroData(WeatherData weatherData) {
        if (BaseUtils.isNull(weatherData.daily)) {
            return null;
        }
        WeatherData.DailyEntity dailyEntity = weatherData.daily.get(0);

        AstroView.AstroData astroData = new AstroView.AstroData();

        astroData.sunSet = dailyEntity.sunSet;
        astroData.sunRise = dailyEntity.sunRise;
        if (!BaseUtils.isNull(weatherData.now)) {
            astroData.pressure = weatherData.now.pressure;
            astroData.windSpeed = weatherData.now.windSpeed;
            astroData.windDirection = weatherData.now.windDirection;
        }

        return astroData;
    }

    @Override
    public void onShowMessage(Message message) {
        Messages msg = (Messages) message;
        switch (msg.what) {
            case Messages.MSG_LOCATION:
                boolean success = msg.arg1 == 1;
                if (!success) {
                    Toast.makeText(CoreManager.getContext(), R.string.weather_add_city_hand_mode, Toast.LENGTH_LONG).show();
                    SearchActivity.navigationFromApplication(CoreManager.getContext());
                } else {
                    String cityId = (String) msg.obj;
                    CoreManager.getImpl(ICityProvider.class).saveCurrentCityId(cityId);
                    updateWeather(cityId);
                }
                break;
        }
    }
}
