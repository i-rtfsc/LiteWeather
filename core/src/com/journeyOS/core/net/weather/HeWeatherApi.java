package com.journeyOS.core.net.weather;


import com.journeyOS.core.bean.weather.AqiEntity;
import com.journeyOS.core.bean.weather.HeWeather;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HeWeatherApi {

    @GET("weather")
    Observable<Response<HeWeather>> getWeather(@Query("key") String key,
                                               @Query("location") String location);


    @GET("air/now")
    Observable<Response<AqiEntity>> getAqi(@Query("key") String key, @Query("location") String location);

}