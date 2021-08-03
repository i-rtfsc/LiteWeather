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


package com.journeyOS.core.database.city;

import android.content.Context;

import com.journeyOS.base.persistence.FileHelper;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.SmartLog;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.db.DataResultListener;
import com.journeyOS.core.api.db.ICityProvider;
import com.journeyOS.core.api.db.IGlobalProvider;
import com.journeyOS.core.async.AsyncManager;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.DBHelper;
import com.journeyOS.core.database.WeatherDatabase;
import com.journeyOS.core.database.global.Global;
import com.journeyOS.literouter.annotation.ARouterInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@ARouterInject(api = ICityProvider.class)
public class CityRepositoryImpl implements ICityProvider {
    private static final String TAG = CityRepositoryImpl.class.getSimpleName();
    private Context mContext;
    private CityDao mCityDao;
    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        mContext = CoreManager.getContext();
        WeatherDatabase database = DBHelper.getDefault().getRoomDatabaseBuilder(mContext, WeatherDatabase.class, DBConfigs.DB_NAME);
        mCityDao = database.cityDao();
    }

    @Override
    public void searchCity(final String cityId, boolean finishedOnMainThread, final DataResultListener listener) {
        new AsyncManager.AsyncJobBuilder<City>()
                .withExecutor(mExecutorService)
                .doInBackground(new AsyncManager.AsyncAction<City>() {
                    @Override
                    public City doAsync() {
                        return mCityDao.searchCity(cityId);
                    }
                })
                .doWhenFinished(new AsyncManager.AsyncResultAction<City>() {
                    @Override
                    public void onResult(City city) {
                        listener.onResult(city);
                    }
                }, finishedOnMainThread)
                .create()
                .start();
    }

    @Override
    public void searchCity(final String cityName, final String county, boolean finishedOnMainThread, final DataResultListener listener) {
        new AsyncManager.AsyncJobBuilder<City>()
                .withExecutor(mExecutorService)
                .doInBackground(new AsyncManager.AsyncAction<City>() {
                    @Override
                    public City doAsync() {
                        return mCityDao.searchCity(cityName, county);
                    }
                })
                .doWhenFinished(new AsyncManager.AsyncResultAction<City>() {
                    @Override
                    public void onResult(City city) {
                        listener.onResult(city);
                    }
                }, finishedOnMainThread)
                .create()
                .start();
    }

    @Override
    public void loadCities() {
        SmartLog.d(TAG, "start");
        CoreManager.getImpl(IGlobalProvider.class).getGlobal(DBConfigs.Global.CITY_INIT, DBConfigs.Global.CITY_INIT_DEFAULT, false,
                new DataResultListener() {
                    @Override
                    public void onResult(Object result) {
                        Global global = (Global) result;
                        boolean init = global.getBoolean();
                        if (init) {
                            SmartLog.d(TAG, "city table has been inited!");
                        } else {
                            initCity();
                        }
                        return;
                    }
                });


    }

    private void initCity() {
        new AsyncManager.AsyncJobBuilder<Boolean>()
                .withExecutor(mExecutorService)
                .doInBackground(new AsyncManager.AsyncAction<Boolean>() {
                    @Override
                    public Boolean doAsync() {
                        try {
                            String cities = FileHelper.assetFile2String("china_citys.json", mContext);
                            JSONArray jsonArray = new JSONArray(cities);
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
                            mCityDao.insertCities(allCitys);
                            return true;
                        } catch (JSONException e) {
                            SmartLog.d(TAG, "parse city info fail " + e);
                            return false;
                        }
                    }
                })
                .doWhenFinished(new AsyncManager.AsyncResultAction<Boolean>() {
                    @Override
                    public void onResult(Boolean success) {
                        if (success) {
                            CoreManager.getImpl(IGlobalProvider.class).saveGlobal(DBConfigs.Global.CITY_INIT, true);
                        }
                    }
                }, false)
                .create()
                .start();

    }

    /**
     * a-z排序
     */
    private static class CityComparator implements Comparator<City> {

        @Override
        public int compare(City cityLeft, City cityRight) {

            char a = cityLeft.countryEn.charAt(0);
            char b = cityRight.countryEn.charAt(0);

            return a - b;
        }
    }

    public static class CityEntry {
        /**
         * name : 北京
         * name_en : beijing
         * city : {"name":"北京","county":[{"name":"北京","code":"CN101010100","name_en":"beijing"},{"name":"海淀","code":"CN101010200","name_en":"haidian"},{"name":"朝阳","code":"CN101010300","name_en":"chaoyang"},{"name":"顺义","code":"CN101010400","name_en":"shunyi"},{"name":"怀柔","code":"CN101010500","name_en":"huairou"},{"name":"通州","code":"CN101010600","name_en":"tongzhou"},{"name":"昌平","code":"CN101010700","name_en":"changping"},{"name":"延庆","code":"CN101010800","name_en":"yanqing"},{"name":"丰台","code":"CN101010900","name_en":"fengtai"},{"name":"石景山","code":"CN101011000","name_en":"shijingshan"},{"name":"大兴","code":"CN101011100","name_en":"daxing"},{"name":"房山","code":"CN101011200","name_en":"fangshan"},{"name":"密云","code":"CN101011300","name_en":"miyun"},{"name":"门头沟","code":"CN101011400","name_en":"mentougou"},{"name":"平谷","code":"CN101011500","name_en":"pinggu"}]}
         */
        public String name;
        public String name_en;
        public List<CityBean> city;

        public static class CityBean {
            /**
             * name : 北京
             * county : [{"name":"北京","code":"CN101010100","name_en":"beijing"},{"name":"海淀","code":"CN101010200","name_en":"haidian"},{"name":"朝阳","code":"CN101010300","name_en":"chaoyang"},{"name":"顺义","code":"CN101010400","name_en":"shunyi"},{"name":"怀柔","code":"CN101010500","name_en":"huairou"},{"name":"通州","code":"CN101010600","name_en":"tongzhou"},{"name":"昌平","code":"CN101010700","name_en":"changping"},{"name":"延庆","code":"CN101010800","name_en":"yanqing"},{"name":"丰台","code":"CN101010900","name_en":"fengtai"},{"name":"石景山","code":"CN101011000","name_en":"shijingshan"},{"name":"大兴","code":"CN101011100","name_en":"daxing"},{"name":"房山","code":"CN101011200","name_en":"fangshan"},{"name":"密云","code":"CN101011300","name_en":"miyun"},{"name":"门头沟","code":"CN101011400","name_en":"mentougou"},{"name":"平谷","code":"CN101011500","name_en":"pinggu"}]
             */
            public String name;
            public List<CountyBean> county;

            public static class CountyBean {
                /**
                 * name : 北京
                 * code : CN101010100
                 * name_en : beijing
                 */
                public String name;
                public String code;
                public String name_en;
            }
        }
    }

}
