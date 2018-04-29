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

package com.journeyOS.liteweather.api;


import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.security.Base64Util;
import com.journeyOS.core.AppHttpClient;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.cityprovider.City;
import com.journeyOS.core.api.cityprovider.ICityProvider;
import com.journeyOS.core.api.weatherprovider.WeatherData;
import com.journeyOS.core.base.StatusDataResource;
import com.journeyOS.literouter.annotation.ARouterInject;
import com.journeyOS.liteweather.R;
import com.journeyOS.liteweather.entity.AqiEntity;
import com.journeyOS.liteweather.entity.HeWeather;
import com.journeyOS.liteweather.entity.WeatherTransverter;
import com.journeyOS.liteweather.repository.WeatherRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Response;

import static com.journeyOS.base.Constant.WEATHER_KEY;


@ARouterInject(api = IFetchWeather.class)
public class WeatherFetchImpl implements IFetchWeather {
    private static final String TAG = WeatherFetchImpl.class.getSimpleName();
    private static final String $ = "$";
    private NetWeatherApi mNetWeatherApi;
    private AtomicReference<String> mStringAtomicReference = new AtomicReference<>($);
    private String[] mWeatherKeys;

    @Override
    public void onCreate() {
        mNetWeatherApi = AppHttpClient.getInstance().getService(NetWeatherApi.class);
        mWeatherKeys = CoreManager.getContext().getResources().getStringArray(R.array.weather_key_values);
    }

    @Override
    public void queryWeather(final String cityId) {
        if (cityId == null || cityId.equals(mStringAtomicReference.get())) {
            return;
        }
        final int scheduleWhich = SpUtils.getInstant().getInt(WEATHER_KEY, 0);
        final String weatherKey = Base64Util.fromBase64(mWeatherKeys[scheduleWhich]);

        mStringAtomicReference.set(cityId);

        WeatherRepository.getInstance().getWeatherWorkHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    WeatherRepository.getInstance().updateWeather(cityId, StatusDataResource.<WeatherData>loading());

                    Call<HeWeather> weatherEntityCall = mNetWeatherApi.getWeather(weatherKey, cityId);
                    //和风天气不支持县级空气质量
                    City currentCity = CoreManager.getImpl(ICityProvider.class).searchCity(cityId);
                    String cityName = cityId;
                    if (currentCity != null) {
                        cityName = currentCity.cityName;
                    }
                    Call<AqiEntity> aqiEntityCall = mNetWeatherApi.getAqi(weatherKey, cityName);

                    Response<HeWeather> heWeatherResponse = weatherEntityCall.execute();
                    Response<AqiEntity> aqiEntityResponse = aqiEntityCall.execute();
                    if (heWeatherResponse.isSuccessful()) {
                        WeatherData weatherData = WeatherTransverter.convertFromHeWeather(heWeatherResponse.body(), aqiEntityResponse.body());
                        WeatherRepository.getInstance().updateWeather(cityId, StatusDataResource.success(weatherData));
                    } else {
                        WeatherRepository.getInstance().updateWeather(cityId, StatusDataResource.<WeatherData>error(heWeatherResponse.errorBody().string()));
                    }
                } catch (Exception e) {
                    WeatherRepository.getInstance().updateWeather(cityId, StatusDataResource.<WeatherData>error(CoreManager.getContext().getResources().getString(R.string.weather_refresh_fail)));
                }

                mStringAtomicReference.set($);
            }

        });
    }

    @Override
    public void queryWeather(List<String> citys) {

    }
}
