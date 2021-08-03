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

package com.journeyOS.plugins.city.repository;


import android.os.Handler;
import androidx.annotation.WorkerThread;

import com.journeyOS.core.api.ICoreApi;
import com.journeyOS.core.api.cityprovider.City;

import java.util.List;
import java.util.Set;

public interface ICityRepositoryApi extends ICoreApi {
    /**
     * 通过名字或者拼音搜索
     *
     * @param cityName 市,county 县
     * @return 结果
     */
    @WorkerThread
    City searchCity(final String cityName, final String county);

    @WorkerThread
    City searchCity(String cityId);

    @WorkerThread
    List<City> matchingCity(final String keyword);

    @WorkerThread
    List<City> queryAllCities();

    Handler getCityWorkHandler();

    void saveCurrentCityId(String cityId);

    void saveCityId(String cityId);

    void deleteCityId(String cityId);

    void saveCitiesId(Set<String> citiesId);

    Set<String> getCitiesId();

    String getCurrentCityId();

    boolean hadCurrentCityId();

    void loadCitys();
}
