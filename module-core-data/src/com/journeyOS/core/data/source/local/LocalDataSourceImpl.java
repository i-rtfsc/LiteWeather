package com.journeyOS.core.data.source.local;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.journeyOS.core.data.source.LocalDataSource;
import com.journeyOS.core.data.source.local.base.DBConfigs;
import com.journeyOS.core.data.source.local.base.DBHelper;
import com.journeyOS.core.data.source.local.base.WeatherDatabase;
import com.journeyOS.core.data.source.local.city.City;
import com.journeyOS.core.data.source.local.city.CityDao;
import com.journeyOS.core.data.source.local.weather.Weather;
import com.journeyOS.core.data.source.local.weather.WeatherDao;
import com.journeyOS.liteframework.store.settings.SettingsManager;
import com.journeyOS.liteframework.utils.FileUtils;
import com.journeyOS.liteframework.utils.JsonUtils;
import com.journeyOS.liteframework.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 本地数据源，配合Room框架使用
 */
public class LocalDataSourceImpl implements LocalDataSource {
    private static final String TAG = LocalDataSourceImpl.class.getSimpleName();
    private volatile static LocalDataSourceImpl INSTANCE = null;

    private WeatherDao mWeatherDao;
    private CityDao mCityDao;

    public static LocalDataSourceImpl getInstance() {
        if (INSTANCE == null) {
            synchronized (LocalDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSourceImpl();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private LocalDataSourceImpl() {
        //数据库Helper构建
        Context context = Utils.getContext();
        WeatherDatabase database = DBHelper.getDefault().getRoomDatabaseBuilder(context, WeatherDatabase.class, DBConfigs.DB_NAME);
        mWeatherDao = database.weatherDao();
        mCityDao = database.cityDao();

        //init city if needed
        initCity();
    }


    @Override
    public void put(@NonNull String key, @NonNull Object defaultValue) {
        SettingsManager.put(key, defaultValue);
    }

    @Override
    public String getString(@NonNull String key, @NonNull String defaultValue) {
        return SettingsManager.getString(key, defaultValue);
    }

    @Override
    public int getInt(@NonNull String key, int defaultValue) {
        return SettingsManager.getInt(key, defaultValue);
    }

    @Override
    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return SettingsManager.getBoolean(key, defaultValue);
    }

    @Override
    public float getFloat(@NonNull String key, float defaultValue) {
        return SettingsManager.getFloat(key, defaultValue);
    }

    @Override
    public City searchCity(String cityName, String adm1) {
        return mCityDao.searchCity(cityName, adm1);
    }

    @Override
    public City searchCity(String locationId) {
        return mCityDao.searchCity(locationId);
    }

    @Override
    public List<City> matchCity(String key) {
        return mCityDao.matchCity(key);
    }

    @Override
    public List<City> getAllCity() {
        return mCityDao.getAll();
    }

    @Override
    public void saveWeather(Weather weather) {
        mWeatherDao.saveWeather(weather);
    }

    @Override
    public Weather getWeather(String locationId) {
        return mWeatherDao.getWeather(locationId);
    }

    @Override
    public void deleteWeather(String locationId) {
        mWeatherDao.deleteWeather(locationId);
    }

    @Override
    public List<Weather> getAllWeather() {
        return mWeatherDao.getAll();
    }

    private void initCity() {
        boolean city_init = getBoolean(DBConfigs.Settings.CITY_INIT, DBConfigs.Settings.CITY_INIT_DEFAULT);
        Log.d(TAG, "init = " + city_init);
        if (city_init) {
            return;
        }

        Observable<China> observable = Observable.just(getChina());
        observable.subscribeOn(Schedulers.io())
                .subscribe(new Observer<China>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull China china) {
                        List<City> cityList = china.getCities();
                        if (cityList != null && cityList.size() > 0) {
                            //Insert here
                            mCityDao.insertCities(china.getCities());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "init city error = " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "init city complete");
                        put(DBConfigs.Settings.CITY_INIT, true);

                        //保存浦东新区
                        City city = mCityDao.searchCity(DBConfigs.Settings.LOCATION_ID_DEFAULT);
                        put(DBConfigs.Settings.LOCATION_ID, JsonUtils.toJson(city));
                    }
                });
    }

    private China getChina() {
        China china = new China();
        try {
            String cities = FileUtils.assetFile2String(Utils.getContext(), "china_city.json");
            JSONArray jsonArray = new JSONArray(cities);
            List<City> allCitys = new ArrayList<>();
            for (int index = 0; index < jsonArray.length(); index++) {
                JSONObject cityObject = jsonArray.getJSONObject(index);
                China.CityBean cityBean = JsonUtils.fromJson(cityObject.toString(), China.CityBean.class);
                City city = new City();
                city.locationId = cityBean.locationId;
                city.locationNameEn = cityBean.locationNameEn;
                city.cityName = cityBean.cityName;
                city.countryCode = cityBean.countryCode;
                city.countryEn = cityBean.countryEn;
                city.adm1En = cityBean.adm1En;
                city.adm1 = cityBean.adm1;
                city.adm2En = cityBean.adm2En;
                city.adm2 = cityBean.adm2;
                city.latitude = cityBean.latitude;
                city.longitude = cityBean.longitude;
                allCitys.add(city);
            }
            china.setCities(allCitys);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return china;
    }

    public static class China {
        List<City> cities;

        public List<City> getCities() {
            return cities;
        }

        public void setCities(List<City> cities) {
            this.cities = cities;
        }

        public class CityBean {
            @SerializedName("location_id")
            public String locationId;
            @SerializedName("location_name_en")
            public String locationNameEn;
            @SerializedName("city_name")
            public String cityName;
            @SerializedName("country_code")
            public String countryCode;
            @SerializedName("country_en")
            public String countryEn;
            @SerializedName("adm1_en")
            public String adm1En;
            @SerializedName("adm1")
            public String adm1;
            @SerializedName("adm2_en")
            public String adm2En;
            @SerializedName("adm2")
            public String adm2;
            @SerializedName("latitude")
            public String latitude;
            @SerializedName("longitude")
            public String longitude;
        }

    }

}
