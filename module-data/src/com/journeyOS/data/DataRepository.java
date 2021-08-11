package com.journeyOS.data;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.journeyOS.data.source.HttpDataSource;
import com.journeyOS.data.source.LocalDataSource;
import com.journeyOS.data.source.local.city.City;
import com.journeyOS.data.source.local.weather.Weather;
import com.journeyOS.data.entity.AirNow;
import com.journeyOS.data.entity.Indices;
import com.journeyOS.data.entity.NowBase;
import com.journeyOS.data.entity.Sun;
import com.journeyOS.data.entity.WeatherDaily;
import com.journeyOS.data.entity.WeatherHourly;
import com.journeyOS.liteframework.base.BaseModel;
import com.journeyOS.liteframework.rxhttp.HttpObserver;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class DataRepository extends BaseModel implements HttpDataSource, LocalDataSource {
    private volatile static DataRepository INSTANCE = null;
    private final HttpDataSource mHttpDataSource;

    private final LocalDataSource mLocalDataSource;

    private DataRepository(@NonNull HttpDataSource httpDataSource, @NonNull LocalDataSource localDataSource) {
        this.mHttpDataSource = httpDataSource;
        this.mLocalDataSource = localDataSource;
    }

    public static DataRepository getInstance(HttpDataSource httpDataSource, LocalDataSource localDataSource) {
        if (INSTANCE == null) {
            synchronized (DataRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataRepository(httpDataSource, localDataSource);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<Response<NowBase>> test(String key, String location) {
        return mHttpDataSource.test(key, location);
    }

    @Override
    public void getWeatherNow(HttpObserver<NowBase> httpObserver, String key, String location) {
        mHttpDataSource.getWeatherNow(httpObserver, key, location);
    }

    @Override
    public void getWeather7D(HttpObserver<WeatherDaily> httpObserver, String key, String location) {
        mHttpDataSource.getWeather7D(httpObserver, key, location);
    }

    @Override
    public void getWeather24Hourly(HttpObserver<WeatherHourly> httpObserver, String key, String location) {
        mHttpDataSource.getWeather24Hourly(httpObserver, key, location);
    }

    @Override
    public void get1DIndices(HttpObserver<Indices> httpObserver, String key, String location) {
        mHttpDataSource.get1DIndices(httpObserver, key, location);
    }

    @Override
    public void getAirNow(HttpObserver<AirNow> httpObserver, String key, String location) {
        mHttpDataSource.getAirNow(httpObserver, key, location);
    }

    @Override
    public void getSun(HttpObserver<Sun> httpObserver, String key, String location, String date) {
        mHttpDataSource.getSun(httpObserver, key, location, date);
    }

    @Override
    public void put(@NonNull String key, @NonNull Object defaultValue) {
        mLocalDataSource.put(key, defaultValue);
    }

    @Override
    public String getString(@NonNull String key, @NonNull String defaultValue) {
        return mLocalDataSource.getString(key, defaultValue);
    }

    @Override
    public int getInt(@NonNull String key, int defaultValue) {
        return mLocalDataSource.getInt(key, defaultValue);
    }

    @Override
    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return mLocalDataSource.getBoolean(key, defaultValue);
    }

    @Override
    public float getFloat(@NonNull String key, float defaultValue) {
        return mLocalDataSource.getFloat(key, defaultValue);
    }

    @Override
    public City searchCity(String cityName, String adm1) {
        return mLocalDataSource.searchCity(cityName, adm1);
    }

    @Override
    public City searchCity(String locationId) {
        return mLocalDataSource.searchCity(locationId);
    }

    @Override
    public List<City> matchCity(String key) {
        return mLocalDataSource.matchCity(key);
    }

    @Override
    public List<City> getAllCity() {
        return mLocalDataSource.getAllCity();
    }

    @Override
    public void saveWeather(Weather weather) {
        mLocalDataSource.saveWeather(weather);
    }

    @Override
    public Weather getWeather(String locationId) {
        return mLocalDataSource.getWeather(locationId);
    }

    @Override
    public void deleteWeather(String locationId) {
        mLocalDataSource.deleteWeather(locationId);
    }

    @Override
    public List<Weather> getAllWeather() {
        return mLocalDataSource.getAllWeather();
    }
}
