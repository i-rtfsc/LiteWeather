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
import android.support.annotation.WorkerThread;

import com.journeyOS.base.persistence.FileHelper;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.cityprovider.City;
import com.journeyOS.core.repository.DBHelper;
import com.journeyOS.literouter.Router;
import com.journeyOS.literouter.annotation.ARouterInject;
import com.journeyOS.litetask.TaskScheduler;
import com.journeyOS.plugins.city.repository.db.CityDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.journeyOS.base.Constant.ALL_CITIES;
import static com.journeyOS.base.Constant.CITY_INITED;
import static com.journeyOS.base.Constant.CURRENT_CITY;


@ARouterInject(api = ICityRepositoryApi.class)
public class CityRepositoryImpl implements ICityRepositoryApi {
    private static final String TAG = CityRepositoryImpl.class.getSimpleName();
    private static final String CITY_DB_NAME = "china_city";
    private CityDatabase mCityDatabase;
    private Handler mCityHandler;
    private List<City> mAllCityData = new ArrayList<>();

    @Override
    public void onCreate() {
        mCityDatabase = DBHelper.provider(CityDatabase.class, CITY_DB_NAME);
        mCityHandler = TaskScheduler.provideHandler(TAG);
        Router.getDefault().register(this);
    }

    @Override
    public void saveCurrentCityId(String cityId) {
        SpUtils.getInstant().put(CURRENT_CITY, cityId);
        notifyCurrentCityChanged(cityId);
    }

    @Override
    public void saveCityId(String cityId) {
        Set<String> set = SpUtils.getInstant().getStringSet(ALL_CITIES);
        if (BaseUtils.isNull(set)) {
            set = new HashSet<String>();
        }
        if (set.contains(cityId)) {
            return;
        }

        set.add(cityId);
//        SpUtils.getInstant().remove(ALL_CITIES);
        SpUtils.getInstant().put(ALL_CITIES, new HashSet(), true);
        SpUtils.getInstant().put(ALL_CITIES, set, true);
        notifyCityChanged(cityId, true);
    }

    @Override
    public void deleteCityId(String cityId) {
        Set<String> set = SpUtils.getInstant().getStringSet(ALL_CITIES);
        if (BaseUtils.isNull(set)) {
            return;
        }
        if (set.contains(cityId)) {
            set.remove(cityId);
//            SpUtils.getInstant().remove(ALL_CITIES);
            SpUtils.getInstant().put(ALL_CITIES, new HashSet(), true);
            SpUtils.getInstant().put(ALL_CITIES, set);
            notifyCityChanged(cityId, false);
        }
    }

    @Override
    public void saveCitiesId(Set<String> citiesId) {
//        SpUtils.getInstant().remove(ALL_CITIES);
        SpUtils.getInstant().put(ALL_CITIES, new HashSet(), true);
        SpUtils.getInstant().put(ALL_CITIES, citiesId);
    }

    @Override
    public Set<String> getCitiesId() {
        return SpUtils.getInstant().getStringSet(ALL_CITIES);
    }

    @Override
    public String getCurrentCityId() {
        return SpUtils.getInstant().getString(CURRENT_CITY, CURRENT_CITY);
    }

    @Override
    public boolean hadCurrentCityId() {
        return !getCurrentCityId().equals(CURRENT_CITY);
    }


    @Override
    public void loadCitys() {
        boolean cityInited = SpUtils.getInstant().getBoolean(CITY_INITED, false);
        if (cityInited) {
            return;
        }

        mCityHandler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    String citys = FileHelper.assetFile2String("china_citys.txt", CoreManager.getContext());
                    JSONArray jsonArray = new JSONArray(citys);
                    List<City> allCitys = new ArrayList<>();

                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject cityObject = jsonArray.getJSONObject(index);
                        CityEntry cityEntry = JsonHelper.fromJson(cityObject.toString(), CityEntry.class);

                        for (CityEntry.CityBean cityBean : cityEntry.city) {
                            for (CityEntry.CityBean.CountyBean county : cityBean.county) {
                                City city = new City();
                                city.province = cityEntry.name;
                                city.provinceEn = cityEntry.name_en;
                                city.cityName = cityBean.name;
                                city.cityId = county.code;
                                city.country = county.name;
                                city.countryEn = county.name_en;

                                allCitys.add(city);
                            }
                        }
                    }

                    Collections.sort(allCitys, new CityComparator());

                    mCityDatabase.cityDao().insertCities(allCitys);

                    SpUtils.getInstant().getBoolean(CITY_INITED, true);

                } catch (Exception e) {
                    LogUtils.d(TAG, "parse city info fail " + e);
                }

            }
        });
    }

    /**
     * 通过名字或者拼音搜索
     *
     * @param cityName 市,county 县
     * @return 结果
     */
    @Override
    @WorkerThread
    public City searchCity(final String cityName, final String county) {
        return mCityDatabase.cityDao().searchCity(cityName, county);
    }

    @Override
    @WorkerThread
    public City searchCity(final String cityId) {
        return mCityDatabase.cityDao().searchCity(cityId);
    }

    @Override
    @WorkerThread
    public List<City> matchingCity(String keyword) {
        return mCityDatabase.cityDao().matchCity(keyword);
    }

    @Override
    public Handler getCityWorkHandler() {
        return mCityHandler;
    }

    @Override
    @WorkerThread
    public List<City> queryAllCities() {
        if (mAllCityData.size() > 0) {
            return mAllCityData;
        }
        mAllCityData = mCityDatabase.cityDao().getAll();
        return mAllCityData;
    }

    /**
     * a-z排序
     */
    private class CityComparator implements Comparator<City> {

        @Override
        public int compare(City cityLeft, City cityRight) {

            char a = cityLeft.countryEn.charAt(0);
            char b = cityRight.countryEn.charAt(0);

            return a - b;
        }
    }

    private void notifyCurrentCityChanged(String cityId) {
        Messages msg = new Messages();
        msg.what = Messages.MSG_CURRENT_CITY_CHANGED;
        msg.obj = cityId;
        Router.getDefault().post(msg);
    }

    private void notifyCityChanged(String cityId, boolean isAdd) {
        Messages msg = new Messages();
        msg.what = isAdd ? Messages.MSG_CITY_ADD : Messages.MSG_CITY_DELETE;
        msg.obj = cityId;
        Router.getDefault().post(msg);
    }
}
