package com.journeyOS.liteweather.data.source.http;

import com.journeyOS.liteframework.http.RetrofitClient;
import com.journeyOS.liteframework.rxhttp.HttpCoreManager;
import com.journeyOS.liteframework.rxhttp.HttpObserver;
import com.journeyOS.liteweather.data.source.HttpDataSource;
import com.journeyOS.liteweather.data.source.http.service.HeWeatherApiService;
import com.journeyOS.liteweather.entity.AirNow;
import com.journeyOS.liteweather.entity.Indices;
import com.journeyOS.liteweather.entity.NowBase;
import com.journeyOS.liteweather.entity.Sun;
import com.journeyOS.liteweather.entity.WeatherDaily;
import com.journeyOS.liteweather.entity.WeatherHourly;

import io.reactivex.Observable;
import retrofit2.Response;

public class HttpDataSourceImpl implements HttpDataSource {
    private static final String TAG = "solo-debug";
    //    private static final String BASE_URL_HEWEATHER = "https://api.qweather.com/v7/";
    private static final String BASE_URL_HEWEATHER = "https://devapi.qweather.com/v7/";
    private HeWeatherApiService apiService;

    private volatile static HttpDataSourceImpl INSTANCE = null;

    public static HttpDataSourceImpl getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDataSourceImpl();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private HttpDataSourceImpl() {
        this.apiService = RetrofitClient.getInstance(true).getService(BASE_URL_HEWEATHER, HeWeatherApiService.class);
    }

    @Override
    public Observable<Response<NowBase>> test(String key, String location) {
        return apiService.getWeatherNow(key, location);
    }

    @Override
    public void getWeatherNow(HttpObserver<NowBase> httpObserver, final String key, final String location) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<NowBase, Response<NowBase>>() {
            @Override
            public Observable<Response<NowBase>> createObservable(boolean forceNetWork) {
                return apiService.getWeatherNow(key, location);
            }
        }, httpObserver, false);
    }

    @Override
    public void getWeather7D(HttpObserver<WeatherDaily> httpObserver, final String key, final String location) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<WeatherDaily, Response<WeatherDaily>>() {
            @Override
            public Observable<Response<WeatherDaily>> createObservable(boolean forceNetWork) {
                return apiService.getWeather7D(key, location);
            }
        }, httpObserver, false);
    }

    @Override
    public void getWeather24Hourly(HttpObserver<WeatherHourly> httpObserver, final String key, final String location) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<WeatherHourly, Response<WeatherHourly>>() {
            @Override
            public Observable<Response<WeatherHourly>> createObservable(boolean forceNetWork) {
                return apiService.getWeather24Hourly(key, location);
            }
        }, httpObserver, false);
    }

    @Override
    public void get1DIndices(final HttpObserver<Indices> httpObserver, final String key, final String location) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<Indices, Response<Indices>>() {
            @Override
            public Observable<Response<Indices>> createObservable(boolean forceNetWork) {
                return apiService.get1DIndices(key, location);
            }
        }, httpObserver, false);
    }

    @Override
    public void getAirNow(HttpObserver<AirNow> httpObserver, final String key, final String location) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<AirNow, Response<AirNow>>() {
            @Override
            public Observable<Response<AirNow>> createObservable(boolean forceNetWork) {
                return apiService.getAirNow(key, location);
            }
        }, httpObserver, false);
    }

    @Override
    public void getSun(HttpObserver<Sun> httpObserver, final String key, final String location, final String date) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<Sun, Response<Sun>>() {
            @Override
            public Observable<Response<Sun>> createObservable(boolean forceNetWork) {
                return apiService.getSun(key, location, date);
            }
        }, httpObserver, false);
    }


}
