package com.journeyOS.core.data.source;

import com.journeyOS.core.entity.AirNow;
import com.journeyOS.core.entity.Indices;
import com.journeyOS.core.entity.NowBase;
import com.journeyOS.core.entity.Sun;
import com.journeyOS.core.entity.WeatherDaily;
import com.journeyOS.core.entity.WeatherHourly;
import com.journeyOS.liteframework.rxhttp.HttpObserver;

import io.reactivex.Observable;
import retrofit2.Response;


public interface HttpDataSource {

    Observable<Response<NowBase>> test(String key, String location);

    void getWeatherNow(HttpObserver<NowBase> httpObserver, final String key, final String location);

    void getWeather7D(HttpObserver<WeatherDaily> httpObserver, final String key, final String location);

    void getWeather24Hourly(HttpObserver<WeatherHourly> httpObserver, final String key, final String location);

    void get1DIndices(HttpObserver<Indices> httpObserver, final String key, final String location);

    void getAirNow(HttpObserver<AirNow> httpObserver, final String key, final String location);

    void getSun(HttpObserver<Sun> httpObserver, final String key, final String location, final String date);
}
