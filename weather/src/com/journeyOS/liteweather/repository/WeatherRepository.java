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

package com.journeyOS.liteweather.repository;


import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.weatherprovider.WeatherData;
import com.journeyOS.core.base.StatusDataResource;
import com.journeyOS.core.repository.DBHelper;
import com.journeyOS.literouter.Router;
import com.journeyOS.litetask.TaskScheduler;
import com.journeyOS.liteweather.repository.db.Weather;
import com.journeyOS.liteweather.repository.db.WeatherDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.journeyOS.liteweather.repository.db.Configs.WEATHER_DB_NAME;


public class WeatherRepository {
    private static final String TAG = WeatherRepository.class.getSimpleName();

    private WeatherDatabase mWeatherDatabase;
    private static final AtomicReference<WeatherRepository> INSTANCE_REFERENCE = new AtomicReference<>();

    private MutableLiveData<StatusDataResource<WeatherData>> mWeatherDataLiveData;
    private Handler mWeatherWorkHandler;

    private WeatherRepository() {
        mWeatherDatabase = DBHelper.provider(WeatherDatabase.class, WEATHER_DB_NAME);
        mWeatherWorkHandler = TaskScheduler.provideHandler(TAG);
        mWeatherDataLiveData = new MutableLiveData<>();

    }

    public static WeatherRepository getInstance() {

        for (; ; ) {
            WeatherRepository current = INSTANCE_REFERENCE.get();
            if (current != null) {
                return current;
            }
            current = new WeatherRepository();
            if (INSTANCE_REFERENCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    public Handler getWeatherWorkHandler() {
        return mWeatherWorkHandler;
    }

    @MainThread
    public MutableLiveData<StatusDataResource<WeatherData>> getWeatherObserver() {
        return mWeatherDataLiveData;
    }

    @Nullable
    public WeatherData getCachedWeatherData() {
        return mWeatherDataLiveData.getValue() == null ? null : mWeatherDataLiveData.getValue().data;
    }


    @MainThread
    public void saveWeatherAsync(final String cityId, final StatusDataResource<WeatherData> statusDataResource) {
        mWeatherWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                updateWeather(cityId, statusDataResource);
            }
        });
    }

    @WorkerThread
    public void updateWeather(String cityId, final StatusDataResource<WeatherData> statusDataResource) {
        if (StatusDataResource.Status.SUCCESS.equals(statusDataResource.status)) {
            try {
                WeatherData weatherData = statusDataResource.data;
                Weather weather = new Weather();
                weather.cityId = weatherData.cityId;
                weather.weatherJson = JsonHelper.toJson(weatherData);

                mWeatherDatabase.weatherDao().saveWeather(weather);
            } catch (Exception e) {
                LogUtils.e(TAG, "updateWeather error = " + e);
            }
        } else if (StatusDataResource.Status.LOADING.equals(statusDataResource.status)) {
            try {
                WeatherData weatherData = JsonHelper.fromJson(mWeatherDatabase.weatherDao().fetchWeather(cityId).weatherJson, WeatherData.class);
                if (weatherData != null) {
                    statusDataResource.data = weatherData;
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "no cache hit");
            }

        }

        mWeatherDataLiveData.postValue(statusDataResource);

        Messages msg = new Messages();
        msg.what = Messages.MSG_UPDATE_NOTIFICATION;
        Router.getDefault().post(msg);

    }


    @MainThread
    public void deleteWeather(final String cityId) {
        if (cityId == null) {
            return;
        }
        mWeatherWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                mWeatherDatabase.weatherDao().deleteWeather(cityId);
            }
        });

    }

    @WorkerThread
    public List<WeatherData> getFollowedWeather() {
        List<WeatherData> followedWeather = new ArrayList<>();
        try {
            for (Weather weather : mWeatherDatabase.weatherDao().fetchFollowedWeather()) {
                WeatherData weatherData = JsonHelper.fromJson(weather.weatherJson, WeatherData.class);
                followedWeather.add(weatherData);
            }

        } catch (Exception e) {
            LogUtils.d(TAG, "getFollowedWeather error = " + e);
        }

        return followedWeather;
    }

    public WeatherData getDbWeather(String cityId) {
        try {
            WeatherData weatherData = JsonHelper.fromJson(mWeatherDatabase.weatherDao().fetchWeather(cityId).weatherJson, WeatherData.class);
            if (weatherData != null) {
                return weatherData;
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "no cache hit");
        }
        return null;
    }
}
