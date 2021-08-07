package com.journeyOS.core.data.source.http;

import com.journeyOS.core.data.source.HttpDataSource;
import com.journeyOS.core.data.source.http.service.HeWeatherApiService;
import com.journeyOS.core.entity.AirNow;
import com.journeyOS.core.entity.Indices;
import com.journeyOS.core.entity.NowBase;
import com.journeyOS.core.entity.Sun;
import com.journeyOS.core.entity.WeatherDaily;
import com.journeyOS.core.entity.WeatherHourly;
import com.journeyOS.liteframework.http.RetrofitClient;
import com.journeyOS.liteframework.rxhttp.HttpCoreManager;
import com.journeyOS.liteframework.rxhttp.HttpObserver;

import io.reactivex.Observable;
import retrofit2.Response;

//https://widget-page.qweather.net/h5/index.html?md=0123456&bg=1&lc=accu&key=f59e252b2cfe41b8a4ad7691e72a909e&v=_1628390411182
public class HttpDataSourceImpl implements HttpDataSource {
    private static final String TAG = HttpDataSourceImpl.class.getSimpleName();
    //private static final String BASE_URL_HEWEATHER = "https://api.qweather.com/v7/";
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
        }, httpObserver);
    }

    @Override
    public void getWeather7D(HttpObserver<WeatherDaily> httpObserver, final String key, final String location) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<WeatherDaily, Response<WeatherDaily>>() {
            @Override
            public Observable<Response<WeatherDaily>> createObservable(boolean forceNetWork) {
                return apiService.getWeather7D(key, location);
            }
        }, httpObserver);
    }

    @Override
    public void getWeather24Hourly(HttpObserver<WeatherHourly> httpObserver, final String key, final String location) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<WeatherHourly, Response<WeatherHourly>>() {
            @Override
            public Observable<Response<WeatherHourly>> createObservable(boolean forceNetWork) {
                return apiService.getWeather24Hourly(key, location);
            }
        }, httpObserver);
    }

    @Override
    public void get1DIndices(final HttpObserver<Indices> httpObserver, final String key, final String location) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<Indices, Response<Indices>>() {
            @Override
            public Observable<Response<Indices>> createObservable(boolean forceNetWork) {
                return apiService.get1DIndices(key, location);
            }
        }, httpObserver);
    }

    @Override
    public void getAirNow(HttpObserver<AirNow> httpObserver, final String key, final String location) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<AirNow, Response<AirNow>>() {
            @Override
            public Observable<Response<AirNow>> createObservable(boolean forceNetWork) {
                return apiService.getAirNow(key, location);
            }
        }, httpObserver);
    }

    @Override
    public void getSun(HttpObserver<Sun> httpObserver, final String key, final String location, final String date) {
        HttpCoreManager.executeRxHttp(new HttpCoreManager.IObservableCreator<Sun, Response<Sun>>() {
            @Override
            public Observable<Response<Sun>> createObservable(boolean forceNetWork) {
                return apiService.getSun(key, location, date);
            }
        }, httpObserver);
    }


}
