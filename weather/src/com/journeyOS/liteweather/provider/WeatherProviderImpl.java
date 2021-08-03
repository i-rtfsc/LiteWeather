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

package com.journeyOS.liteweather.provider;

import androidx.lifecycle.LiveData;
import android.content.Context;

import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.weatherprovider.IWeatherProvider;
import com.journeyOS.core.bean.weather.WeatherData;
import com.journeyOS.core.base.StatusDataResource;
import com.journeyOS.literouter.annotation.ARouterInject;
import com.journeyOS.liteweather.api.IFetchWeather;
import com.journeyOS.liteweather.repository.WeatherRepository;
import com.journeyOS.liteweather.scheduleJob.PollingUtils;

import java.util.List;


@ARouterInject(api = IWeatherProvider.class)
public class WeatherProviderImpl implements IWeatherProvider {

    @Override
    public void onCreate() {

    }

    @Override
    public LiveData<StatusDataResource<WeatherData>> getWeatherData() {
        return WeatherRepository.getInstance().getWeatherObserver();
    }

    @Override
    public List<WeatherData> fetchFollowedWeather() {
        return WeatherRepository.getInstance().getFollowedWeather();
    }

    @Override
    public WeatherData fetchWeather(String cityId) {
        return WeatherRepository.getInstance().getDbWeather(cityId);
    }

    @Override
    public void updateWeather(String cityId) {
        CoreManager.getImpl(IFetchWeather.class).queryWeather(cityId);
    }

    @Override
    public void deleteWeather(String cityId) {
        WeatherRepository.getInstance().deleteWeather(cityId);
    }

    @Override
    public void startService(Context context, boolean allowPoll) {
        PollingUtils.startService(context, allowPoll);
    }

}
