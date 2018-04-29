/*
 * Copyright (c) 2018 anqi.huang@outlook.com.
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

package com.journeyOS.core.api.cityprovider;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.WorkerThread;

import com.journeyOS.core.api.ICoreApi;

import java.util.Set;


public interface ICityProvider extends ICoreApi {
    @WorkerThread
    City searchCity(String cityId);

    @WorkerThread
    City searchCity(String cityName, final String county);

    Handler getCityWorkHandler();

    void navigationSearchActivity(Context context);

    void saveCurrentCityId(String cityId);

    void saveCityId(String cityId);

    void deleteCityId(String cityId);

    void saveCitiesId(Set<String> citiesId);

    Set<String> getCitiesId();

    String getCurrentCityId();

    boolean hadCurrentCityId();

    void loadCitys();
}
