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


package com.journeyOS.core.database.weather;

import android.content.Context;

import com.journeyOS.base.utils.SmartLog;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.db.DataResultListener;
import com.journeyOS.core.api.db.IWeatherProvider;
import com.journeyOS.core.async.AsyncManager;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.DBHelper;
import com.journeyOS.core.database.WeatherDatabase;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@ARouterInject(api = IWeatherProvider.class)
public class WeatherRepositoryImpl implements IWeatherProvider {
    private static final String TAG = WeatherRepositoryImpl.class.getSimpleName();
    private Context mContext;
    private WeatherDao mWeatherDao;
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        mContext = CoreManager.getContext();
        WeatherDatabase database = DBHelper.getDefault().getRoomDatabaseBuilder(mContext, WeatherDatabase.class, DBConfigs.DB_NAME);
        mWeatherDao = database.weatherDao();
    }

    @Override
    public void getWeather(final String cityId, boolean finishedOnMainThread, final DataResultListener listener) {
        new AsyncManager.AsyncJobBuilder<Weather>()
                .withExecutor(mExecutorService)
                .doInBackground(new AsyncManager.AsyncAction<Weather>() {
                    @Override
                    public Weather doAsync() {
                        return mWeatherDao.getWeather(cityId);
                    }
                })
                .doWhenFinished(new AsyncManager.AsyncResultAction<Weather>() {
                    @Override
                    public void onResult(Weather weather) {
                        listener.onResult(weather);
                    }
                }, finishedOnMainThread)
                .create()
                .start();
    }

    @Override
    public void updateWeather(final String cityId, final String data) {
        new AsyncManager.AsyncJobBuilder<Weather>()
                .withExecutor(mExecutorService)
                .doInBackground(new AsyncManager.AsyncAction<Weather>() {
                    @Override
                    public Weather doAsync() {
                        Weather weather = mWeatherDao.getWeather(cityId);
                        if (weather == null) {
                            weather = new Weather();
                            weather.cityId = cityId;
                        }
                        weather.data = data;
                        weather.time = String.valueOf(System.currentTimeMillis());
                        mWeatherDao.saveWeather(weather);
                        return weather;
                    }
                })
                .doWhenFinished(new AsyncManager.AsyncResultAction<Weather>() {
                    @Override
                    public void onResult(Weather weather) {
                        if (weather != null) {
                            SmartLog.d(TAG, "update weather time = [" + weather.time + "]");
                        }
                    }
                }, false)
                .create()
                .start();
    }

    @Override
    public void deleteWeather(final String cityId) {
        new AsyncManager.AsyncJobBuilder<Weather>()
                .withExecutor(mExecutorService)
                .doInBackground(new AsyncManager.AsyncAction<Weather>() {
                    @Override
                    public Weather doAsync() {
                        Weather weather = mWeatherDao.getWeather(cityId);
                        if (weather != null) {
                            mWeatherDao.deleteWeather(cityId);
                        }
                        return weather;
                    }
                })
                .doWhenFinished(new AsyncManager.AsyncResultAction<Weather>() {
                    @Override
                    public void onResult(Weather weather) {
                        if (weather != null) {
                            SmartLog.d(TAG, "delete weather success");
                        }
                    }
                }, false)
                .create()
                .start();
    }
}
