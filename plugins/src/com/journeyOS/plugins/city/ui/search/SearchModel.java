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

package com.journeyOS.plugins.city.ui.search;

import androidx.lifecycle.MutableLiveData;

import com.journeyOS.plugins.city.repository.ICityRepositoryApi;
import com.journeyOS.plugins.city.ui.adapter.CityInfoData;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.cityprovider.City;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.litetask.TaskScheduler;

import java.util.ArrayList;
import java.util.List;


public class SearchModel extends BaseViewModel {

    private MutableLiveData<List<CityInfoData>> mAllCityData = new MutableLiveData<>();

    private MutableLiveData<List<CityInfoData>> mMatchedCityData = new MutableLiveData<>();

    @Override
    protected void onCreate() {

    }

    void getAllCities() {
        CoreManager.getImpl(ICityRepositoryApi.class).getCityWorkHandler().post(new Runnable() {
            @Override
            public void run() {
                List<City>  allCity = CoreManager.getImpl(ICityRepositoryApi.class).queryAllCities();
                if(allCity !=null) {

                    List<CityInfoData> cityInfoDatas = new ArrayList<>();
                    String lastInitial = "";
                    for(City city : allCity) {
                        CityInfoData cityInfoData =  new CityInfoData(city.country,city.countryEn,city.cityId);
                        String currentInitial = city.countryEn.substring(0, 1);
                        if (!lastInitial.equals(currentInitial) ) {
                            cityInfoData.setInitial(currentInitial);
                            lastInitial = currentInitial;
                        }
                        cityInfoDatas.add(cityInfoData);
                    }
                    mAllCityData.postValue(cityInfoDatas);
                }
            }
        });
    }

    MutableLiveData<List<CityInfoData>> getAllCityData() {
        return mAllCityData;
    }

    MutableLiveData<List<CityInfoData>> getMatchedCityData() {
        return mMatchedCityData;
    }

    void matchCities(final String key) {
        TaskScheduler.getInstance().getExecutor("TODO").execute(new Runnable() {
            @Override
            public void run() {
                List<City> allCity = CoreManager.getImpl(ICityRepositoryApi.class).matchingCity(key);
                if(allCity !=null ) {

                    List<CityInfoData> cityInfoDatas = new ArrayList<>();
                    for(City city : allCity) {
                        cityInfoDatas.add(new CityInfoData(city.country,city.countryEn,city.cityId));
                    }
                    mMatchedCityData.postValue(cityInfoDatas);
                }
            }
        });
    }
}
