package com.journeyOS.data.source.http.service;

import com.journeyOS.data.entity.AirNow;
import com.journeyOS.data.entity.Indices;
import com.journeyOS.data.entity.NowBase;
import com.journeyOS.data.entity.Sun;
import com.journeyOS.data.entity.WeatherDaily;
import com.journeyOS.data.entity.WeatherHourly;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;


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
