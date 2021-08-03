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

package com.journeyOS.plugins.city.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import com.journeyOS.core.CoreManager;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.cityprovider.ICityProvider;
import com.journeyOS.core.api.weatherprovider.IWeatherProvider;
import com.journeyOS.core.bean.weather.WeatherData;
import com.journeyOS.core.base.StatusDataResource;
import com.journeyOS.core.location.ILocationApi;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.literouter.RouterListener;
import com.journeyOS.literouter.RouterMsssage;
import com.journeyOS.litetask.TaskScheduler;
import com.journeyOS.plugins.city.ui.adapter.FollowedCityData;
import com.journeyOS.plugins.city.ui.adapter.FollowedCityHolder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class CityModel extends BaseViewModel implements RouterListener {
    private MutableLiveData<List<FollowedCityData>> mFollowedWeather = new MutableLiveData<>();

    @Override
    protected void onCreate() {
        mFollowedWeather.setValue(new CopyOnWriteArrayList<FollowedCityData>());
        CoreManager.getImpl(IWeatherProvider.class).getWeatherData().observe(this, new Observer<StatusDataResource<WeatherData>>() {
            @Override
            public void onChanged(@Nullable StatusDataResource<WeatherData> statusDataResource) {
                if (statusDataResource.isSucceed()) {
                    onWeather(statusDataResource.data);
                }
            }

        });
    }

    public LiveData<List<FollowedCityData>> getFollowedWeather() {
        return mFollowedWeather;
    }


    private void fetchFollowedWeather() {

        TaskScheduler.getInstance().getExecutor("TODO").execute(new Runnable() {
            @Override
            public void run() {
                List<WeatherData> weatherData = CoreManager.getImpl(IWeatherProvider.class).fetchFollowedWeather();
                parseFollowedWeathers(weatherData);
            }
        });
    }

    public void deleteFollowedWeather(String cityId) {
        CoreManager.getImpl(ICityProvider.class).deleteCityId(cityId);//
        CoreManager.getImpl(IWeatherProvider.class).deleteWeather(cityId);

        if (CoreManager.getImpl(ICityProvider.class).getCurrentCityId().equals(cityId)) {
            String locationId = CoreManager.getImpl(ILocationApi.class).getLocatedCityId();
//            CoreManager.getImpl(ICityProvider.class).saveCurrentCityId(locationId);
            CoreManager.getImpl(IWeatherProvider.class).updateWeather(locationId);
        }

        List<FollowedCityData> followedCityDatas = mFollowedWeather.getValue();
        for (FollowedCityData followedCityData : followedCityDatas) {
            if (followedCityData.getCityId().equals(cityId)) {
                followedCityDatas.remove(followedCityData);
                break;
            }
        }

        mFollowedWeather.setValue(followedCityDatas);
    }


    @MainThread
    private void parseFollowedWeathers(List<WeatherData> weatherDatas) {
        List<FollowedCityData> followedCityDatas = mFollowedWeather.getValue();
        followedCityDatas.clear();

        for (int index = 0; index < weatherDatas.size(); index++) {
            WeatherData weatherData = weatherDatas.get(index);
            if (weatherData != null) {
                if (weatherData.cityId.equals(CoreManager.getImpl(ILocationApi.class).getLocatedCityId())) {
                    followedCityDatas.add(0, new FollowedCityData(weatherData, FollowedCityHolder.BLUR_IMAGE[index % FollowedCityHolder.BLUR_IMAGE.length]));
                } else {
                    followedCityDatas.add(new FollowedCityData(weatherData, FollowedCityHolder.BLUR_IMAGE[index % FollowedCityHolder.BLUR_IMAGE.length]));
                }
            }
        }
        mFollowedWeather.postValue(followedCityDatas);
    }

    private void onWeather(WeatherData weatherData) {
        boolean exist = false;
        List<FollowedCityData> followedCityDatas = mFollowedWeather.getValue();
        for (FollowedCityData followedCityData : followedCityDatas) {
            if (followedCityData.getCityId().equals(weatherData.cityId)) {
                followedCityData.update(weatherData);
                exist = true;
                break;
            }
        }

        if (!exist) {
            if (CoreManager.getImpl(ILocationApi.class).getLocatedCityId().equals(weatherData.cityId)) {
                followedCityDatas.add(0, new FollowedCityData(weatherData, FollowedCityHolder.BLUR_IMAGE[(followedCityDatas.size() + 1) % FollowedCityHolder.BLUR_IMAGE.length]));
            } else {
                followedCityDatas.add(new FollowedCityData(weatherData, FollowedCityHolder.BLUR_IMAGE[(followedCityDatas.size() + 1) % FollowedCityHolder.BLUR_IMAGE.length]));
            }
        }

        mFollowedWeather.setValue(followedCityDatas);
    }

    @Override
    public void onShowMessage(RouterMsssage message) {
        Messages msg = (Messages) message;
        switch (msg.what) {
            case Messages.MSG_LOCATION:
                fetchFollowedWeather();
                break;
        }
    }
}
