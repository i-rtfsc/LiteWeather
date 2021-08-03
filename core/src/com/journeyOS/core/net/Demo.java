package com.journeyOS.core.net;

import android.util.Log;

import androidx.annotation.NonNull;

import com.journeyOS.base.security.Base64Util;
import com.journeyOS.core.bean.weather.AqiEntity;
import com.journeyOS.core.bean.weather.HeWeather;
import com.journeyOS.core.bean.weather.WeatherData;
import com.journeyOS.core.bean.weather.WeatherTransverter;
import com.journeyOS.core.http.HttpCoreManager;
import com.journeyOS.core.http.HttpObserver;
import com.journeyOS.core.http.HttpResponse;
import com.journeyOS.core.net.weather.HeWeatherApi;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;


public class Demo {
    private static String TAG = "solo";

//    private static String _KEY = "N2UwYzI2ZTc0ZjM4NGRlNTllZmI3YTg2NTY1YTFjMGY";
//    private static String _KEY = "ZGVmOWE1MDczMjhlNGNkMzk1ZDk4M2ZlMjU4OTU4NmU";
    private static String _KEY = "OGFlZWM3NzAxNzcyNGI1MThhNWYwYmE1ZDE4ODg4MjA";

    private static final String KEY = Base64Util.fromBase64(_KEY);

    private static String CITY = "CN101020100";

    public static void test() {
        HttpObserver<HeWeather> httpObserver = new HttpObserver<HeWeather>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<HeWeather> response) {
                Log.e(TAG, "onSuccess() called with: response = [" + response + "]");
                HeWeather heWeather = response.body();
                Log.e(TAG, "onSuccess() called with: heWeather = [" + heWeather + "]");
                test2(heWeather);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e(TAG, "onError() called with: error = [" + error + "]");
            }

        };

        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<HeWeather, Response<HeWeather>>() {
            @Override
            public Observable<Response<HeWeather>> createObservable(boolean forceNetWork) {
                Log.d(TAG, "test() called with: KEY = [" + KEY + "], CITY = [" + CITY + "]");
                return AppHttpClient.getInstance().getService(HeWeatherApi.class).getWeather(KEY, CITY);
            }
        }, httpObserver, false);
    }

    public static void test2(final HeWeather heWeather) {
        HttpObserver<AqiEntity> httpObserver = new HttpObserver<AqiEntity>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<AqiEntity> response) {
                AqiEntity aqiEntity = response.body();
                Log.e(TAG, "onSuccess(2) aqiEntity = [" + aqiEntity + "]");
                WeatherData weatherData = WeatherTransverter.convertFromHeWeather(heWeather, aqiEntity);
                WeatherData.DailyEntity dailyEntity = weatherData.daily.get(0);
                Log.e(TAG, "onSuccess(2) weatherData = [" + dailyEntity.toString() + "]");
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e(TAG, "onError(2) called with: error = [" + error + "]");
            }

        };

        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<AqiEntity, Response<AqiEntity>>() {
            @Override
            public Observable<Response<AqiEntity>> createObservable(boolean forceNetWork) {
                Log.d(TAG, "test(2) called with: KEY = [" + KEY + "], CITY = [" + CITY + "]");
                return AppHttpClient.getInstance().getService(HeWeatherApi.class).getAqi(KEY, CITY);
            }
        }, httpObserver, false);
    }
}
