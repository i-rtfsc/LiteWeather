package com.journeyOS.core.data.source.http.service;

import com.journeyOS.core.entity.AirNow;
import com.journeyOS.core.entity.Indices;
import com.journeyOS.core.entity.NowBase;
import com.journeyOS.core.entity.Sun;
import com.journeyOS.core.entity.WeatherDaily;
import com.journeyOS.core.entity.WeatherHourly;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

// bc0418b57b2d4918819d3974ac1285d9     3000次 7天预报
// 537664b7e2124b3c845bc0b51278d4af     1000次 3天预报
//def9a507328e4cd395d983fe2589586e
//7e0c26e74f384de59efb7a86565a1c0f

//8aeec77017724b518a5f0ba5d1888820
//https://dev.qweather.com/docs/api/
public interface HeWeatherApiService {

    //https://dev.qweather.com/docs/api/weather/weather-now/
    //实时天气
    @GET("weather/now")
    Observable<Response<NowBase>> getWeatherNow(@Query("key") String key,
                                                @Query("location") String location);

    //https://dev.qweather.com/docs/api/weather/weather-daily-forecast/
    //逐天天气预报
    @GET("weather/7d?")
    Observable<Response<WeatherDaily>> getWeather7D(@Query("key") String key,
                                                    @Query("location") String location);

    //https://dev.qweather.com/docs/api/weather/weather-hourly-forecast/
    //逐小时天气预报
    @GET("weather/24h?")
    Observable<Response<WeatherHourly>> getWeather24Hourly(@Query("key") String key,
                                                           @Query("location") String location);

    //https://dev.qweather.com/docs/api/indices/
    //天气生活指数
    @GET("indices/1d?type=0")
    Observable<Response<Indices>> get1DIndices(@Query("key") String key,
                                               @Query("location") String location);

    //https://dev.qweather.com/docs/api/air/air-now/
    //实时空气质量
    @GET("air/now")
    Observable<Response<AirNow>> getAirNow(@Query("key") String key,
                                           @Query("location") String location);

    //https://dev.qweather.com/docs/api/astronomy/sunrise-sunset/
    //日出日落（商业版才有这个api）
    @GET("astronomy/sun")
    Observable<Response<Sun>> getSun(@Query("key") String key,
                                     @Query("location") String location,
                                     @Query("date") String date);

}
