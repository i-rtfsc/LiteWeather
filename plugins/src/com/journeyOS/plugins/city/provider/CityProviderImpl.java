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

package com.journeyOS.plugins.city.provider;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.WorkerThread;

import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.cityprovider.City;
import com.journeyOS.core.api.cityprovider.ICityProvider;
import com.journeyOS.literouter.annotation.ARouterInject;
import com.journeyOS.plugins.city.repository.ICityRepositoryApi;
import com.journeyOS.plugins.city.ui.search.SearchActivity;

import java.util.Set;


@ARouterInject(api = ICityProvider.class)
public class CityProviderImpl implements ICityProvider {

    @Override
    public void onCreate() {

    }


    @Override
    public City searchCity(String cityId) {
        return CoreManager.getImpl(ICityRepositoryApi.class).searchCity(cityId);
    }

    @WorkerThread
    @Override
    public City searchCity(String cityName, String county) {
        return CoreManager.getImpl(ICityRepositoryApi.class).searchCity(cityName, county);
    }

    @WorkerThread
    @Override
    public Handler getCityWorkHandler() {
        return CoreManager.getImpl(ICityRepositoryApi.class).getCityWorkHandler();
    }

    @Override
    public void navigationSearchActivity(Context context) {
        SearchActivity.navigationActivity(context);
    }

    @Override
    public void saveCurrentCityId(String cityId) {
        CoreManager.getImpl(ICityRepositoryApi.class).saveCurrentCityId(cityId);
    }

    @Override
    public void saveCityId(String cityId) {
        CoreManager.getImpl(ICityRepositoryApi.class).saveCityId(cityId);
    }

    @Override
    public void deleteCityId(String cityId) {
        CoreManager.getImpl(ICityRepositoryApi.class).deleteCityId(cityId);
    }

    @Override
    public void saveCitiesId(Set<String> citiesId) {
        CoreManager.getImpl(ICityRepositoryApi.class).saveCitiesId(citiesId);
    }

    @Override
    public Set<String> getCitiesId() {
        return CoreManager.getImpl(ICityRepositoryApi.class).getCitiesId();
    }

    @Override
    public String getCurrentCityId() {
        return CoreManager.getImpl(ICityRepositoryApi.class).getCurrentCityId();
    }

    @Override
    public boolean hadCurrentCityId() {
        return CoreManager.getImpl(ICityRepositoryApi.class).hadCurrentCityId();
    }


    @Override
    public void loadCitys() {
        CoreManager.getImpl(ICityRepositoryApi.class).loadCitys();
    }

}
